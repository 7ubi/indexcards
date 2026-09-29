package com.x7ubi.indexcards.request.indexcard;

import java.util.List;

public class CreateIndexCardsRequest {

    private Long projectId;

    private List<IndexCardContentRequest> cards;

    public CreateIndexCardsRequest() {}

    public CreateIndexCardsRequest(Long projectId, List<IndexCardContentRequest> cards) {
        this.projectId = projectId;
        this.cards = cards;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<IndexCardContentRequest> getCards() {
        return cards;
    }

    public void setCards(List<IndexCardContentRequest> cards) {
        this.cards = cards;
    }
}
