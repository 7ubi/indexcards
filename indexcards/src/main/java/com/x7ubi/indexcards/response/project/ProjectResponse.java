package com.x7ubi.indexcards.response.project;

import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;

import java.util.List;

public class ProjectResponse {

    private String name;

    private List<IndexCardResponse> indexCardResponses;

    public ProjectResponse() {}

    public ProjectResponse(String name, List<IndexCardResponse> indexCardResponses) {
        this.name = name;
        this.indexCardResponses = indexCardResponses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IndexCardResponse> getIndexCardResponses() {
        return indexCardResponses;
    }

    public void setIndexCardResponses(List<IndexCardResponse> indexCardResponses) {
        this.indexCardResponses = indexCardResponses;
    }
}
