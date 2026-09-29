package com.x7ubi.indexcards.ai;

import com.sun.net.httpserver.HttpServer;
import com.x7ubi.indexcards.config.AiProperties;
import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import com.x7ubi.indexcards.service.ai.CardGenerationInput;
import com.x7ubi.indexcards.service.ai.GeminiCardGenerationClient;
import com.x7ubi.indexcards.service.ai.GeneratedCard;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runs the real HTTP client against a local stub server, so no request reaches the Gemini API.
 */
public class GeminiCardGenerationClientTest {

    private static final String API_KEY = "AIzaSy-user-key-for-tests-0123456789";

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private static final String SUCCESS = """
            {"candidates":[{"content":{"role":"model","parts":[
               {"text":"thinking...","thought":true},
               {"text":"{\\"cards\\":[{\\"question\\":\\"What is $E = mc^2$?\\",\\"answer\\":\\"Mass-energy equivalence\\"}]}"}]},
              "finishReason":"STOP"}],
             "usageMetadata":{"promptTokenCount":1200,"candidatesTokenCount":300}}
            """;

    private static final String BLOCKED_PROMPT = """
            {"promptFeedback":{"blockReason":"PROHIBITED_CONTENT"},"usageMetadata":{"promptTokenCount":10}}
            """;

    private static final String SAFETY_STOP = """
            {"candidates":[{"content":{"parts":[]},"finishReason":"SAFETY"}]}
            """;

    private static final String TRUNCATED = """
            {"candidates":[{"content":{"parts":[{"text":"{\\"cards\\":[{\\"question\\":\\"Q"}]},
              "finishReason":"MAX_TOKENS"}]}
            """;

    private static final String NOT_JSON = """
            {"candidates":[{"content":{"parts":[{"text":"Here are your cards"}]},"finishReason":"STOP"}]}
            """;

    private static final String MODEL = """
            {"name":"models/gemini-3.8-flash","displayName":"Gemini 3.8 Flash"}
            """;

    private static String error(int code, String status, String reason) {
        return "{\"error\":{\"code\":" + code + ",\"message\":\"stub\",\"status\":\"" + status + "\",\"details\":"
                + "[{\"@type\":\"type.googleapis.com/google.rpc.ErrorInfo\",\"reason\":\"" + reason + "\"}]}}";
    }

    private final AtomicInteger requests = new AtomicInteger();
    private volatile String lastPath;
    private volatile String lastBody;
    private volatile String lastApiKey;
    private volatile int status = 200;
    private volatile String response = SUCCESS;
    private HttpServer server;

    private GeminiCardGenerationClient client(String model, String thinkingLevel) {
        String baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
        return new GeminiCardGenerationClient(new AiProperties(
                "", model, thinkingLevel, 30, 20000, 10_485_760, 20, 5, 1, 0, 16000, baseUrl));
    }

    private GeminiCardGenerationClient client() {
        return client("gemini-3.8-flash", "low");
    }

    @BeforeEach
    void startServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            requests.incrementAndGet();
            lastPath = exchange.getRequestURI().getPath();
            lastBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            lastApiKey = exchange.getRequestHeaders().getFirst("x-goog-api-key");
            byte[] body = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, body.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(body);
            }
        });
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop(0);
    }

    private AiGenerationException generateFails() {
        return Assertions.assertThrows(AiGenerationException.class,
                () -> client().generate(API_KEY, new CardGenerationInput("Notes", null, 5)));
    }

    @Test
    public void generateParsesStructuredOutputAndSkipsThoughtsTest() throws Exception {
        List<GeneratedCard> cards = client().generate(API_KEY, new CardGenerationInput("Physics notes", null, 5));

        Assertions.assertEquals(1, cards.size());
        Assertions.assertEquals("What is $E = mc^2$?", cards.getFirst().question());
        Assertions.assertEquals("Mass-energy equivalence", cards.getFirst().answer());
    }

    @Test
    public void requestUsesUserKeyModelSchemaAndThinkingLevelTest() throws Exception {
        client().generate(API_KEY, new CardGenerationInput("Physics notes", null, 5));

        JsonNode request = JSON.readTree(lastBody);
        JsonNode config = request.path("generationConfig");
        Assertions.assertEquals("/v1beta/models/gemini-3.8-flash:generateContent", lastPath);
        Assertions.assertEquals(API_KEY, lastApiKey);
        Assertions.assertEquals("application/json", config.path("responseMimeType").asString());
        Assertions.assertEquals("object", config.path("responseJsonSchema").path("type").asString());
        Assertions.assertEquals(16000, config.path("maxOutputTokens").asInt());
        Assertions.assertEquals("low", config.path("thinkingConfig").path("thinkingLevel").asString());
        Assertions.assertTrue(request.path("systemInstruction").path("parts").get(0).path("text").asString()
                .contains("index cards"));
        Assertions.assertTrue(lastBody.contains("Physics notes"));
        Assertions.assertFalse(lastBody.contains("inlineData"));
    }

    @Test
    public void gemini2RequestHasNoThinkingLevelTest() throws Exception {
        client("gemini-2.5-flash", "low").generate(API_KEY, new CardGenerationInput("Notes", null, 5));

        Assertions.assertTrue(JSON.readTree(lastBody).path("generationConfig").path("thinkingConfig").isMissingNode());
    }

    @Test
    public void emptyThinkingLevelIsOmittedTest() throws Exception {
        client("gemini-3.8-flash", "").generate(API_KEY, new CardGenerationInput("Notes", null, 5));

        Assertions.assertTrue(JSON.readTree(lastBody).path("generationConfig").path("thinkingConfig").isMissingNode());
    }

    @Test
    public void pdfIsSentAsInlineDataBeforeTheTextTest() throws Exception {
        client().generate(API_KEY, new CardGenerationInput("", "%PDF-1.7 test".getBytes(StandardCharsets.US_ASCII), 5));

        JsonNode parts = JSON.readTree(lastBody).path("contents").get(0).path("parts");
        Assertions.assertEquals("application/pdf", parts.get(0).path("inlineData").path("mimeType").asString());
        Assertions.assertTrue(parts.get(1).has("text"));
    }

    @Test
    public void blockedPromptIsMappedToRefusedTest() {
        response = BLOCKED_PROMPT;

        AiGenerationException exception = generateFails();

        Assertions.assertEquals(ErrorMessage.Ai.REFUSED, exception.getMessage());
        Assertions.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getStatus());
    }

    @Test
    public void safetyStopIsMappedToRefusedTest() {
        response = SAFETY_STOP;

        Assertions.assertEquals(ErrorMessage.Ai.REFUSED, generateFails().getMessage());
    }

    @Test
    public void maxTokensIsMappedTest() {
        response = TRUNCATED;

        Assertions.assertEquals(ErrorMessage.Ai.OUTPUT_TRUNCATED, generateFails().getMessage());
    }

    @Test
    public void outputThatIsNotJsonIsMappedToFailedTest() {
        response = NOT_JSON;

        AiGenerationException exception = generateFails();

        Assertions.assertEquals(ErrorMessage.Ai.FAILED, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_GATEWAY, exception.getStatus());
    }

    @Test
    public void invalidKeyIsNotMappedTo401Test() {
        status = 400;
        response = error(400, "INVALID_ARGUMENT", "API_KEY_INVALID");

        AiGenerationException exception = generateFails();

        Assertions.assertEquals(ErrorMessage.Ai.API_KEY_INVALID, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void unauthorizedIsMappedToInvalidKeyTest() {
        status = 401;
        response = error(401, "UNAUTHENTICATED", "ACCESS_TOKEN_TYPE_UNSUPPORTED");

        AiGenerationException exception = generateFails();

        Assertions.assertEquals(ErrorMessage.Ai.API_KEY_INVALID, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    public void otherBadRequestIsMappedToFailedTest() {
        status = 400;
        response = error(400, "INVALID_ARGUMENT", "BAD_SCHEMA");

        Assertions.assertEquals(ErrorMessage.Ai.FAILED, generateFails().getMessage());
    }

    @Test
    public void permissionDeniedIsMappedTest() {
        status = 403;
        response = error(403, "PERMISSION_DENIED", "SERVICE_DISABLED");

        Assertions.assertEquals(ErrorMessage.Ai.API_KEY_FORBIDDEN, generateFails().getMessage());
    }

    @Test
    public void paymentRequiredIsMappedToBillingTest() {
        status = 402;
        response = error(402, "FAILED_PRECONDITION", "BILLING");

        Assertions.assertEquals(ErrorMessage.Ai.BILLING, generateFails().getMessage());
    }

    @Test
    public void rateLimitIsMappedTest() {
        status = 429;
        response = error(429, "RESOURCE_EXHAUSTED", "RATE_LIMIT_EXCEEDED");

        AiGenerationException exception = generateFails();

        Assertions.assertEquals(ErrorMessage.Ai.RATE_LIMITED, exception.getMessage());
        Assertions.assertEquals(HttpStatus.TOO_MANY_REQUESTS, exception.getStatus());
        Assertions.assertEquals(1, requests.get(), "rate limits are not retried");
    }

    @Test
    public void unavailableIsRetriedAndMappedTest() {
        status = 503;
        response = error(503, "UNAVAILABLE", "OVERLOADED");

        Assertions.assertEquals(ErrorMessage.Ai.UNAVAILABLE, generateFails().getMessage());
        Assertions.assertEquals(2, requests.get(), "one retry with maxRetries = 1");
    }

    @Test
    public void verifyApiKeyRetrievesTheModelTest() throws Exception {
        response = MODEL;

        client().verifyApiKey(API_KEY);

        Assertions.assertEquals("/v1beta/models/gemini-3.8-flash", lastPath);
        Assertions.assertEquals(API_KEY, lastApiKey);
    }

    @Test
    public void verifyApiKeyRejectsInvalidKeyTest() {
        status = 400;
        response = error(400, "INVALID_ARGUMENT", "API_KEY_INVALID");

        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class,
                () -> client().verifyApiKey(API_KEY));

        Assertions.assertEquals(ErrorMessage.Ai.API_KEY_INVALID, exception.getMessage());
    }
}
