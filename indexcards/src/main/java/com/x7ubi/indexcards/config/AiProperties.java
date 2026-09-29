package com.x7ubi.indexcards.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Settings of the AI card generation ({@code app.ai.*}). Every user brings their own Gemini API key; the feature is
 * only available when {@code encryptionSecret} is set, because the keys are stored encrypted with it.
 *
 * @param thinkingLevel Gemini 3 thinking level (low/medium/high); empty uses the model default
 * @param baseUrl       only for tests (points the client at a local stub server); empty uses the Gemini API
 */
@ConfigurationProperties(prefix = "app.ai")
public record AiProperties(
        @DefaultValue("") String encryptionSecret,
        @DefaultValue("gemini-3.8-flash") String model,
        @DefaultValue("low") String thinkingLevel,
        @DefaultValue("30") int maxCards,
        @DefaultValue("20000") int maxNotesChars,
        @DefaultValue("10485760") long maxPdfBytes,
        @DefaultValue("20") int maxPdfPages,
        @DefaultValue("120") int timeoutSeconds,
        @DefaultValue("1") int maxRetries,
        @DefaultValue("2000") long retryDelayMillis,
        @DefaultValue("16000") long maxOutputTokens,
        @DefaultValue("") String baseUrl
) {
}
