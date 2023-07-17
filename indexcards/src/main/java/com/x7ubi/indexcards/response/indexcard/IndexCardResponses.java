package com.x7ubi.indexcards.response.indexcard;

import com.x7ubi.indexcards.response.common.ResultResponse;

import java.util.List;

public class IndexCardResponses extends ResultResponse {
    private List<IndexCardResponse> indexCardResponses;

    public List<IndexCardResponse> getIndexCardResponses() {
        return indexCardResponses;
    }

    public void setIndexCardResponses(List<IndexCardResponse> indexCardResponses) {
        this.indexCardResponses = indexCardResponses;
    }
}
