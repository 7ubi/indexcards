package com.x7ubi.indexcards.response.indexcard;

public class IndexCardResponse {
    private String name;

    public IndexCardResponse() {}

    public IndexCardResponse(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
