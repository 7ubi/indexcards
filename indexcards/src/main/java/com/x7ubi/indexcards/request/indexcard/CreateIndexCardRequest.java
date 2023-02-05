package com.x7ubi.indexcards.request.indexcard;

public class CreateIndexCardRequest {
    private String question;

    private String answer;

    public CreateIndexCardRequest() {}

    public CreateIndexCardRequest(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
