package com.x7ubi.indexcards.request.indexcard;

public class DeleteIndexCardRequest {
    Long indexcardId;
    Long projectId;

    public DeleteIndexCardRequest(Long indexcardId, Long projectId) {
        this.indexcardId = indexcardId;
        this.projectId = projectId;
    }

    public DeleteIndexCardRequest() {
    }

    public void setIndexcardId(Long indexcardId) {
        this.indexcardId = indexcardId;
    }

    public Long getIndexcardId() {
        return indexcardId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
