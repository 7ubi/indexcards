package com.x7ubi.indexcards.request.indexcard;

public class DeleteIndexCardRequest {
    Long indexcardId;

    public DeleteIndexCardRequest(Long indexcardId) {
        this.indexcardId = indexcardId;
    }

    public DeleteIndexCardRequest() {
    }

    public void setIndexcardId(Long indexcardId) {
        this.indexcardId = indexcardId;
    }

    public Long getIndexcardId() {
        return indexcardId;
    }
}
