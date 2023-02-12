package com.x7ubi.indexcards.request.indexcard;

public class CreateIndexCardRequest {

    private Long projectId;

    private String question;

    private String answer;

    public CreateIndexCardRequest() {}

    public CreateIndexCardRequest(Long projectId, String question, String answer) {
        this.projectId = projectId;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
