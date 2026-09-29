package com.x7ubi.indexcards.response.ai;

import java.util.List;

public class GeneratedCardsResponse {

    private final List<GeneratedCardResponse> cards;

    public GeneratedCardsResponse(List<GeneratedCardResponse> cards) {
        this.cards = cards;
    }

    public List<GeneratedCardResponse> getCards() {
        return cards;
    }
}
