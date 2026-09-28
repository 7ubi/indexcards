package com.x7ubi.indexcards.response.indexcard;

/**
 * An {@link IndexCardResponse} carrying the card's owning project, for views that mix cards
 * from several projects (e.g. the "study all due" session).
 */
public class DueIndexCardResponse extends IndexCardResponse {

    private Long projectId;

    private String projectName;

    public DueIndexCardResponse() {}

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
