package com.x7ubi.indexcards.service.ai;

import com.x7ubi.indexcards.config.AiProperties;
import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.InterruptedIOException;
import java.net.http.HttpClient;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Generates card suggestions with the Gemini API ({@code generateContent}), authenticated with the requesting user's
 * own API key. Never log the key, notes, PDF content or generated cards here: only status codes, error statuses and
 * token counts.
 */
@Component
public class GeminiCardGenerationClient implements CardGenerationClient {

    private static final Logger logger = LoggerFactory.getLogger(GeminiCardGenerationClient.class);

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private static final String DEFAULT_BASE_URL = "https://generativelanguage.googleapis.com";

    private static final String API_KEY_HEADER = "x-goog-api-key";

    /** Finish and block reasons that mean Gemini declined to answer (as opposed to failing). */
    private static final Set<String> BLOCKED_REASONS = Set.of(
            "SAFETY", "RECITATION", "BLOCKLIST", "PROHIBITED_CONTENT", "SPII", "IMAGE_SAFETY", "OTHER");

    private static final String CARDS_SCHEMA_JSON = """
            {
              "type": "object",
              "properties": {
                "cards": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "question": {"type": "string", "description": "Self-contained question, Markdown/LaTeX allowed"},
                      "answer": {"type": "string", "description": "Short, precise answer, Markdown/LaTeX allowed"}
                    },
                    "required": ["question", "answer"],
                    "additionalProperties": false
                  }
                }
              },
              "required": ["cards"],
              "additionalProperties": false
            }
            """;

    private static final JsonNode CARDS_SCHEMA = JSON.readTree(CARDS_SCHEMA_JSON);

    private final AiProperties properties;

    private final RestClient restClient;

    public GeminiCardGenerationClient(AiProperties properties) {
        this.properties = properties;
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(properties.timeoutSeconds()));
        this.restClient = RestClient.builder()
                .baseUrl(StringUtils.hasText(properties.baseUrl()) ? properties.baseUrl() : DEFAULT_BASE_URL)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public List<GeneratedCard> generate(String apiKey, CardGenerationInput input) throws AiGenerationException {
        String requestBody = JSON.writeValueAsString(buildRequest(input));
        JsonNode response = call(client -> client.post()
                .uri("/v1beta/models/{model}:generateContent", properties.model())
                .header(API_KEY_HEADER, apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class));
        JsonNode usage = response.path("usageMetadata");
        logger.info("Card generation used {} input and {} output tokens",
                usage.path("promptTokenCount").asLong(), usage.path("candidatesTokenCount").asLong());
        return toCards(response);
    }

    @Override
    public void verifyApiKey(String apiKey) throws AiGenerationException {
        // Retrieving the configured model is free, needs a valid key and also proves the model exists.
        call(client -> client.get()
                .uri("/v1beta/models/{model}", properties.model())
                .header(API_KEY_HEADER, apiKey)
                .retrieve()
                .body(String.class));
    }

    /**
     * Runs the request and retries server errors and connection problems up to {@code maxRetries} times.
     */
    private JsonNode call(Function<RestClient, String> request) throws AiGenerationException {
        for (int attempt = 0; ; attempt++) {
            try {
                String body = request.apply(restClient);
                return JSON.readTree(body == null ? "{}" : body);
            } catch (RestClientResponseException e) {
                AiGenerationException error = mapError(e);
                if (error.getStatus() != HttpStatus.SERVICE_UNAVAILABLE || attempt >= properties.maxRetries()) {
                    throw error;
                }
                backOff(attempt);
            } catch (ResourceAccessException e) {
                if (isTimeout(e)) {
                    logger.warn("Gemini API request timed out after {} s", properties.timeoutSeconds());
                    throw new AiGenerationException(ErrorMessage.Ai.TIMEOUT, HttpStatus.GATEWAY_TIMEOUT);
                }
                logger.error("Gemini API connection error: {}", e.getClass().getSimpleName());
                if (attempt >= properties.maxRetries()) {
                    throw new AiGenerationException(ErrorMessage.Ai.FAILED, HttpStatus.BAD_GATEWAY);
                }
                backOff(attempt);
            } catch (JacksonException e) {
                logger.error("Gemini API returned invalid JSON");
                throw new AiGenerationException(ErrorMessage.Ai.FAILED, HttpStatus.BAD_GATEWAY);
            }
        }
    }

    /**
     * Never answers 401: the frontend would log the user out. Gemini reports an invalid key as 400 with the reason
     * API_KEY_INVALID (older behaviour) or as 401.
     */
    private AiGenerationException mapError(RestClientResponseException e) {
        int status = e.getStatusCode().value();
        String body = e.getResponseBodyAsString();
        String error = describeError(body);
        if (status == 401 || (status == 400 && body.contains("API_KEY_INVALID"))) {
            logger.warn("Gemini API rejected the user's API key");
            return new AiGenerationException(ErrorMessage.Ai.API_KEY_INVALID, HttpStatus.BAD_REQUEST);
        }
        if (status == 402) {
            logger.warn("Gemini API rejected the request because of the user's billing status");
            return new AiGenerationException(ErrorMessage.Ai.BILLING, HttpStatus.BAD_REQUEST);
        }
        if (status == 403) {
            logger.warn("The user's API key has no permission for this request: {}", error);
            return new AiGenerationException(ErrorMessage.Ai.API_KEY_FORBIDDEN, HttpStatus.BAD_REQUEST);
        }
        if (status == 429) {
            logger.warn("The user's API key is rate limited: {}", error);
            return new AiGenerationException(ErrorMessage.Ai.RATE_LIMITED, HttpStatus.TOO_MANY_REQUESTS);
        }
        if (status == 504) {
            logger.warn("Gemini API deadline exceeded");
            return new AiGenerationException(ErrorMessage.Ai.TIMEOUT, HttpStatus.GATEWAY_TIMEOUT);
        }
        if (status >= 500) {
            logger.warn("Gemini API temporarily unavailable, status {}, {}", status, error);
            return new AiGenerationException(ErrorMessage.Ai.UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
        }
        logger.error("Gemini API error, status {}, {}", status, error);
        return new AiGenerationException(ErrorMessage.Ai.FAILED, HttpStatus.BAD_GATEWAY);
    }

    private ObjectNode buildRequest(CardGenerationInput input) {
        ObjectNode request = JSON.createObjectNode();
        request.putObject("systemInstruction").putArray("parts").addObject()
                .put("text", CardGenerationPrompt.SYSTEM_PROMPT);

        ObjectNode content = request.putArray("contents").addObject().put("role", "user");
        ArrayNode parts = content.putArray("parts");
        if (input.pdf() != null) {
            // The PDF goes before the text that refers to it.
            parts.addObject().putObject("inlineData")
                    .put("mimeType", "application/pdf")
                    .put("data", Base64.getEncoder().encodeToString(input.pdf()));
        }
        parts.addObject().put("text",
                CardGenerationPrompt.userMessage(input.notes(), input.pdf() != null, input.cardCount()));

        ObjectNode generationConfig = request.putObject("generationConfig")
                .put("responseMimeType", "application/json")
                .put("maxOutputTokens", properties.maxOutputTokens());
        generationConfig.set("responseJsonSchema", CARDS_SCHEMA);
        if (StringUtils.hasText(properties.thinkingLevel()) && supportsThinkingLevel(properties.model())) {
            generationConfig.putObject("thinkingConfig").put("thinkingLevel", properties.thinkingLevel());
        }
        return request;
    }

    private List<GeneratedCard> toCards(JsonNode response) throws AiGenerationException {
        String blockReason = response.path("promptFeedback").path("blockReason").asString("");
        JsonNode candidate = response.path("candidates").path(0);
        if (!blockReason.isEmpty() || candidate.isMissingNode()) {
            logger.warn("Gemini blocked the card generation request, reason {}", blockReason);
            throw new AiGenerationException(ErrorMessage.Ai.REFUSED, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        String finishReason = candidate.path("finishReason").asString("");
        if ("MAX_TOKENS".equals(finishReason)) {
            logger.warn("Card generation hit maxOutputTokens ({})", properties.maxOutputTokens());
            throw new AiGenerationException(ErrorMessage.Ai.OUTPUT_TRUNCATED, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (BLOCKED_REASONS.contains(finishReason)) {
            logger.warn("Gemini declined the card generation request, reason {}", finishReason);
            throw new AiGenerationException(ErrorMessage.Ai.REFUSED, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        // Thought summaries (only sent when requested) are skipped; the other text parts carry the JSON.
        StringBuilder json = new StringBuilder();
        for (JsonNode part : candidate.path("content").path("parts")) {
            if (!part.path("thought").asBoolean(false)) {
                json.append(part.path("text").asString(""));
            }
        }
        List<GeneratedCard> cards = new ArrayList<>();
        try {
            JsonNode list = JSON.readTree(json.toString()).path("cards");
            for (JsonNode card : list) {
                if (!card.path("question").isString() || !card.path("answer").isString()) {
                    throw new IllegalArgumentException("card without question or answer");
                }
                cards.add(new GeneratedCard(card.path("question").asString(), card.path("answer").asString()));
            }
        } catch (JacksonException | IllegalArgumentException e) {
            logger.error("Gemini returned output that does not match the card schema");
            throw new AiGenerationException(ErrorMessage.Ai.FAILED, HttpStatus.BAD_GATEWAY);
        }
        return cards;
    }

    /**
     * Status and message of a Gemini error body. The message describes the error only (e.g. "The model is
     * overloaded"), it never echoes the key or the request content.
     */
    private static String describeError(String body) {
        try {
            JsonNode error = JSON.readTree(body).path("error");
            return error.path("status").asString("") + ": " + error.path("message").asString("");
        } catch (JacksonException e) {
            return "(no error body)";
        }
    }

    /** Waits 2 s, 4 s, ... before the next attempt; an immediate retry rarely helps an overloaded model. */
    private void backOff(int attempt) throws AiGenerationException {
        try {
            Thread.sleep(properties.retryDelayMillis() * (attempt + 1L));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AiGenerationException(ErrorMessage.Ai.UNAVAILABLE, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private static boolean supportsThinkingLevel(String model) {
        // Gemini 2.x models only know thinkingBudget and reject thinkingLevel.
        return !model.startsWith("gemini-2");
    }

    private static boolean isTimeout(Throwable throwable) {
        for (Throwable cause = throwable; cause != null; cause = cause.getCause()) {
            if (cause instanceof HttpTimeoutException || cause instanceof InterruptedIOException) {
                return true;
            }
        }
        return false;
    }
}
