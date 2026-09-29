package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.config.AiProperties;
import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.ai.AiStatusResponse;
import com.x7ubi.indexcards.response.ai.GeneratedCardResponse;
import com.x7ubi.indexcards.response.ai.GeneratedCardsResponse;
import com.x7ubi.indexcards.service.ai.ApiKeyEncryptor;
import com.x7ubi.indexcards.service.ai.CardGenerationClient;
import com.x7ubi.indexcards.service.ai.CardGenerationInput;
import com.x7ubi.indexcards.service.ai.CardGenerationInputValidator;
import com.x7ubi.indexcards.service.ai.GeneratedCard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates index card suggestions with Gemini, using the user's own API key. Nothing is saved here: the user reviews
 * the suggestions and saves the chosen ones via {@link CreateIndexCardService#createIndexCards}. Deliberately not
 * {@code @Transactional}: the LLM call can take minutes and must not hold a database transaction open.
 */
@Service
public class GenerateIndexCardsService extends AbstractIndexCardService {

    private static final Logger logger = LoggerFactory.getLogger(GenerateIndexCardsService.class);

    private final AiProperties properties;

    private final ApiKeyEncryptor apiKeyEncryptor;

    private final CardGenerationClient cardGenerationClient;

    private final CardGenerationInputValidator inputValidator;

    // One running generation per user, so a double click does not bill the user's key twice. Single instance
    // deployment, so an in-memory set is enough.
    private final Set<Long> usersGenerating = ConcurrentHashMap.newKeySet();

    GenerateIndexCardsService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo,
            IndexCardMapper indexCardMapper, UserRepo userRepo, AiProperties properties,
            ApiKeyEncryptor apiKeyEncryptor, CardGenerationClient cardGenerationClient,
            CardGenerationInputValidator inputValidator) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper, userRepo);
        this.properties = properties;
        this.apiKeyEncryptor = apiKeyEncryptor;
        this.cardGenerationClient = cardGenerationClient;
        this.inputValidator = inputValidator;
    }

    public AiStatusResponse getStatus(String username) throws EntityNotFoundException {
        User user = getUser(username);
        boolean enabled = apiKeyEncryptor.isAvailable();
        boolean apiKeyConfigured = enabled && user.getAiApiKeyEncrypted() != null;
        return new AiStatusResponse(enabled, apiKeyConfigured, apiKeyConfigured ? user.getAiApiKeyHint() : null,
                properties.model(), properties.maxCards(), properties.maxNotesChars(), properties.maxPdfBytes(),
                properties.maxPdfPages());
    }

    public GeneratedCardsResponse generateIndexCards(
            String username, Long projectId, String notes, int cardCount, MultipartFile pdf)
            throws EntityNotFoundException, UnauthorizedException, EntityCreationException, AiGenerationException {
        if (!apiKeyEncryptor.isAvailable()) {
            throw new AiGenerationException(ErrorMessage.Ai.DISABLED, HttpStatus.SERVICE_UNAVAILABLE);
        }

        getProjectNotFoundError(projectId);
        User user = getUser(username);
        Project project = projectRepo.findProjectByProjectId(projectId);
        getProjectOwnerError(user, project);
        getProjectArchivedError(project);

        String apiKey = decryptApiKey(user);

        String cleanNotes = inputValidator.validateNotes(notes);
        byte[] pdfBytes = inputValidator.validatePdf(pdf);
        if (cleanNotes.isEmpty() && pdfBytes == null) {
            throw new AiGenerationException(ErrorMessage.Ai.INPUT_EMPTY, HttpStatus.BAD_REQUEST);
        }
        int count = Math.clamp(cardCount, 1, properties.maxCards());

        if (!usersGenerating.add(user.getId())) {
            throw new AiGenerationException(ErrorMessage.Ai.IN_PROGRESS, HttpStatus.TOO_MANY_REQUESTS);
        }
        try {
            List<GeneratedCard> generated = cardGenerationClient.generate(
                    apiKey, new CardGenerationInput(cleanNotes, pdfBytes, count));

            List<GeneratedCardResponse> cards = sanitize(generated, count);
            if (cards.isEmpty()) {
                throw new AiGenerationException(ErrorMessage.Ai.NO_CARDS, HttpStatus.UNPROCESSABLE_ENTITY);
            }
            logger.info("{} index card suggestions were generated", cards.size());
            return new GeneratedCardsResponse(cards);
        } finally {
            usersGenerating.remove(user.getId());
        }
    }

    private String decryptApiKey(User user) throws AiGenerationException {
        if (user.getAiApiKeyEncrypted() == null) {
            throw new AiGenerationException(ErrorMessage.Ai.API_KEY_MISSING, HttpStatus.BAD_REQUEST);
        }
        try {
            return apiKeyEncryptor.decrypt(user.getAiApiKeyEncrypted(), user.getId());
        } catch (GeneralSecurityException e) {
            // Usually AI_KEY_ENCRYPTION_SECRET was changed; the user has to enter the key again.
            logger.error("Stored API key of a user could not be decrypted");
            throw new AiGenerationException(ErrorMessage.Ai.API_KEY_UNREADABLE, HttpStatus.BAD_REQUEST);
        }
    }

    /** Trims, drops empty cards and repeated questions (case-insensitive), and enforces the requested maximum. */
    static List<GeneratedCardResponse> sanitize(List<GeneratedCard> cards, int max) {
        List<GeneratedCardResponse> result = new ArrayList<>();
        Set<String> seenQuestions = new HashSet<>();
        for (GeneratedCard card : cards) {
            if (result.size() >= max) {
                break;
            }
            String question = card.question() == null ? "" : card.question().strip();
            String answer = card.answer() == null ? "" : card.answer().strip();
            if (question.isEmpty() || answer.isEmpty() || !seenQuestions.add(question.toLowerCase(Locale.ROOT))) {
                continue;
            }
            result.add(new GeneratedCardResponse(question, answer));
        }
        return result;
    }
}
