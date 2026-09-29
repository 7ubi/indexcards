package com.x7ubi.indexcards.ai;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.request.ai.SaveApiKeyRequest;
import com.x7ubi.indexcards.response.ai.AiStatusResponse;
import com.x7ubi.indexcards.service.indexcard.GenerateIndexCardsService;
import com.x7ubi.indexcards.service.user.DeleteApiKeyService;
import com.x7ubi.indexcards.service.user.SaveApiKeyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class ApiKeyServiceTest extends AiTestConfig {

    @Autowired
    private SaveApiKeyService saveApiKeyService;

    @Autowired
    private DeleteApiKeyService deleteApiKeyService;

    @Autowired
    private GenerateIndexCardsService generateIndexCardsService;

    private User reloadUser() {
        return this.userRepo.findByUsername(this.user.getUsername()).orElseThrow();
    }

    @Test
    public void saveApiKeyStoresItEncryptedTest() throws Exception {
        this.saveApiKeyService.saveApiKey(this.user.getUsername(), new SaveApiKeyRequest("  " + API_KEY + "\n"));

        verify(this.cardGenerationClient).verifyApiKey(API_KEY);
        User saved = reloadUser();
        Assertions.assertNotNull(saved.getAiApiKeyEncrypted());
        Assertions.assertFalse(saved.getAiApiKeyEncrypted().contains(API_KEY));
        Assertions.assertEquals(API_KEY, this.apiKeyEncryptor.decrypt(saved.getAiApiKeyEncrypted(), saved.getId()));
        Assertions.assertEquals("6789", saved.getAiApiKeyHint());
    }

    @Test
    public void malformedApiKeyIsRejectedWithoutCallingGeminiTest() {
        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class, () ->
                this.saveApiKeyService.saveApiKey(this.user.getUsername(), new SaveApiKeyRequest("my-password")));

        Assertions.assertEquals(ErrorMessage.Ai.API_KEY_INVALID, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verifyNoInteractions(this.cardGenerationClient);
        Assertions.assertNull(reloadUser().getAiApiKeyEncrypted());
    }

    @Test
    public void apiKeyRejectedByGeminiIsNotSavedTest() throws Exception {
        doThrow(new AiGenerationException(ErrorMessage.Ai.API_KEY_INVALID, HttpStatus.BAD_REQUEST))
                .when(this.cardGenerationClient).verifyApiKey(any());

        Assertions.assertThrows(AiGenerationException.class, () ->
                this.saveApiKeyService.saveApiKey(this.user.getUsername(), new SaveApiKeyRequest(API_KEY)));

        Assertions.assertNull(reloadUser().getAiApiKeyEncrypted());
    }

    @Test
    public void deleteApiKeyTest() throws Exception {
        saveApiKey();

        this.deleteApiKeyService.deleteApiKey(this.user.getUsername());

        User saved = reloadUser();
        Assertions.assertNull(saved.getAiApiKeyEncrypted());
        Assertions.assertNull(saved.getAiApiKeyHint());
    }

    @Test
    public void statusShowsOnlyTheHintTest() throws Exception {
        AiStatusResponse withoutKey = this.generateIndexCardsService.getStatus(this.user.getUsername());
        Assertions.assertTrue(withoutKey.isEnabled());
        Assertions.assertFalse(withoutKey.isApiKeyConfigured());
        Assertions.assertNull(withoutKey.getApiKeyHint());

        saveApiKey();

        AiStatusResponse withKey = this.generateIndexCardsService.getStatus(this.user.getUsername());
        Assertions.assertTrue(withKey.isApiKeyConfigured());
        Assertions.assertEquals("6789", withKey.getApiKeyHint());
        Assertions.assertEquals(100, withKey.getMaxNotesChars());
    }
}
