package com.x7ubi.indexcards.response.ai;

public class GeneratedCardResponse {

    private final String question;

    private final String answer;

    public GeneratedCardResponse(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
