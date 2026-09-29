package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.ai.SaveApiKeyRequest;
import com.x7ubi.indexcards.service.ai.ApiKeyEncryptor;
import com.x7ubi.indexcards.service.ai.CardGenerationClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Saves the user's own Gemini API key, encrypted. The key is checked against the Gemini API first, so a typo is
 * reported right away and not only on the first generation. Deliberately not @Transactional: the check is an HTTP
 * call and must not hold a database transaction open.
 */
@Service
public class SaveApiKeyService extends AbstractUserService {

    private static final Logger logger = LoggerFactory.getLogger(SaveApiKeyService.class);

    private static final Pattern API_KEY_FORMAT = Pattern.compile("[A-Za-z0-9_\\-.]{30,250}");

    private static final int HINT_LENGTH = 4;

    private final ApiKeyEncryptor apiKeyEncryptor;

    private final CardGenerationClient cardGenerationClient;

    public SaveApiKeyService(UserRepo userRepo, PasswordEncoder passwordEncoder, ApiKeyEncryptor apiKeyEncryptor,
                             CardGenerationClient cardGenerationClient) {
        super(userRepo, passwordEncoder);
        this.apiKeyEncryptor = apiKeyEncryptor;
        this.cardGenerationClient = cardGenerationClient;
    }

    public void saveApiKey(String username, SaveApiKeyRequest request)
            throws EntityNotFoundException, AiGenerationException {
        if (!apiKeyEncryptor.isAvailable()) {
            throw new AiGenerationException(ErrorMessage.Ai.DISABLED, HttpStatus.SERVICE_UNAVAILABLE);
        }
        User user = getUser(username);

        String apiKey = request.getApiKey() == null ? "" : request.getApiKey().strip();
        if (!API_KEY_FORMAT.matcher(apiKey).matches()) {
            logger.warn(ErrorMessage.Ai.API_KEY_INVALID);
            throw new AiGenerationException(ErrorMessage.Ai.API_KEY_INVALID, HttpStatus.BAD_REQUEST);
        }
        cardGenerationClient.verifyApiKey(apiKey);

        user.setAiApiKeyEncrypted(apiKeyEncryptor.encrypt(apiKey, user.getId()));
        user.setAiApiKeyHint(apiKey.substring(apiKey.length() - HINT_LENGTH));
        userRepo.save(user);

        logger.info("Gemini API key was saved");
    }
}
