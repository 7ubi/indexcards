package com.x7ubi.indexcards.ai;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import com.x7ubi.indexcards.indexcard.IndexCardTestConfig;
import com.x7ubi.indexcards.request.ai.SaveApiKeyRequest;
import com.x7ubi.indexcards.service.indexcard.GenerateIndexCardsService;
import com.x7ubi.indexcards.service.user.SaveApiKeyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * No AI_KEY_ENCRYPTION_SECRET configured: the feature is off.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=USER"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AiDisabledTest extends IndexCardTestConfig {

    @Autowired
    private GenerateIndexCardsService generateIndexCardsService;

    @Autowired
    private SaveApiKeyService saveApiKeyService;

    @Test
    public void statusIsDisabledTest() throws Exception {
        Assertions.assertFalse(this.generateIndexCardsService.getStatus(this.user.getUsername()).isEnabled());
    }

    @Test
    public void generateIsRejectedTest() {
        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class, () ->
                this.generateIndexCardsService.generateIndexCards(
                        this.user.getUsername(), this.projects.getFirst().getId(), "notes", 5, null));

        Assertions.assertEquals(ErrorMessage.Ai.DISABLED, exception.getMessage());
        Assertions.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exception.getStatus());
    }

    @Test
    public void savingAKeyIsRejectedTest() {
        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class, () ->
                this.saveApiKeyService.saveApiKey(this.user.getUsername(),
                        new SaveApiKeyRequest("AIzaSy-user-key-for-tests-0123456789")));

        Assertions.assertEquals(ErrorMessage.Ai.DISABLED, exception.getMessage());
    }
}
