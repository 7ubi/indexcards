package com.x7ubi.indexcards.response.indexcard;

import com.x7ubi.indexcards.models.Assessment;

public class IndexCardResponse {
    private Long indexCardId;

    private String question;

    private String answer;

    private Assessment assessment;

    public IndexCardResponse() {}

    public IndexCardResponse(Long indexCardId, String question, String answer, Assessment assessment) {
        this.indexCardId = indexCardId;
        this.question = question;
        this.answer = answer;
        this.assessment = assessment;
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

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
}
