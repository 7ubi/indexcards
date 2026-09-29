package com.x7ubi.indexcards.service.ai;

import com.x7ubi.indexcards.exceptions.AiGenerationException;

import java.util.List;

/**
 * Seam to the LLM. The only implementation talks to the Gemini API with the user's own key; tests replace it with a
 * Mockito mock. Implementations map every provider error to an {@link AiGenerationException}.
 */
public interface CardGenerationClient {

    List<GeneratedCard> generate(String apiKey, CardGenerationInput input) throws AiGenerationException;

    /**
     * Makes a cheap authenticated request, so an invalid key is rejected when it is saved rather than later.
     */
    void verifyApiKey(String apiKey) throws AiGenerationException;
}
