package com.x7ubi.indexcards.request.ai;

public class SaveApiKeyRequest {

    private String apiKey;

    public SaveApiKeyRequest() {}

    public SaveApiKeyRequest(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
