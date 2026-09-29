package com.x7ubi.indexcards.ai;

import com.x7ubi.indexcards.indexcard.IndexCardTestConfig;
import com.x7ubi.indexcards.service.ai.ApiKeyEncryptor;
import com.x7ubi.indexcards.service.ai.CardGenerationClient;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * AI feature enabled (encryption secret set) with the Gemini API client mocked, so no test calls the real API.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        // Own database: this context has other properties, so Spring keeps it cached next to the default test
        // context, and a shared in-memory "testdb" would leak rows between the two.
        "spring.datasource.url=jdbc:h2:mem:aitestdb;NON_KEYWORDS=USER",
        "app.ai.encryption-secret=AQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQE=",
        "app.ai.max-notes-chars=100"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class AiTestConfig extends IndexCardTestConfig {

    protected static final String API_KEY = "AIzaSy-user-key-for-tests-0123456789";

    @MockitoBean
    protected CardGenerationClient cardGenerationClient;

    @Autowired
    protected ApiKeyEncryptor apiKeyEncryptor;

    protected void saveApiKey() {
        this.user.setAiApiKeyEncrypted(this.apiKeyEncryptor.encrypt(API_KEY, this.user.getId()));
        this.user.setAiApiKeyHint("6789");
        this.user = this.userRepo.save(this.user);
    }
}
