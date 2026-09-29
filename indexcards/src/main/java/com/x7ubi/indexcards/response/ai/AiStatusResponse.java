package com.x7ubi.indexcards.response.ai;

/**
 * Whether the AI generation is available on this server and whether the user has saved an API key. The key itself is
 * never returned, only its last characters ({@code apiKeyHint}).
 */
public class AiStatusResponse {

    private final boolean enabled;

    private final boolean apiKeyConfigured;

    private final String apiKeyHint;

    private final String model;

    private final int maxCards;

    private final int maxNotesChars;

    private final long maxPdfBytes;

    private final int maxPdfPages;

    public AiStatusResponse(boolean enabled, boolean apiKeyConfigured, String apiKeyHint, String model, int maxCards,
                            int maxNotesChars, long maxPdfBytes, int maxPdfPages) {
        this.enabled = enabled;
        this.apiKeyConfigured = apiKeyConfigured;
        this.apiKeyHint = apiKeyHint;
        this.model = model;
        this.maxCards = maxCards;
        this.maxNotesChars = maxNotesChars;
        this.maxPdfBytes = maxPdfBytes;
        this.maxPdfPages = maxPdfPages;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isApiKeyConfigured() {
        return apiKeyConfigured;
    }

    public String getApiKeyHint() {
        return apiKeyHint;
    }

    public String getModel() {
        return model;
    }

    public int getMaxCards() {
        return maxCards;
    }

    public int getMaxNotesChars() {
        return maxNotesChars;
    }

    public long getMaxPdfBytes() {
        return maxPdfBytes;
    }

    public int getMaxPdfPages() {
        return maxPdfPages;
    }
}
