package com.x7ubi.indexcards.request.indexcard;

public class EditIndexCardRequest {

    private Long indexCardId;

    private String question;

    private String answer;

    public EditIndexCardRequest() {
    }

    public EditIndexCardRequest(Long indexCardId, String question, String answer) {
        this.indexCardId = indexCardId;
        this.question = question;
        this.answer = answer;
    }

    public Long getIndexCardId() {
        return indexCardId;
    }

    public void setIndexCardId(Long indexCardId) {
        this.indexCardId = indexCardId;
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
