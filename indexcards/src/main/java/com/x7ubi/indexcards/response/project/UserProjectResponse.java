package com.x7ubi.indexcards.response.project;

import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;

import java.util.ArrayList;

public class UserProjectResponse {
    private String name;

    private ArrayList<IndexCardResponse> indexCardResponses;

    public UserProjectResponse(String name, ArrayList<IndexCardResponse> indexCardResponses) {
        this.name = name;
        this.indexCardResponses = indexCardResponses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<IndexCardResponse> getIndexCardResponses() {
        return indexCardResponses;
    }

    public void setIndexCardResponses(ArrayList<IndexCardResponse> indexCardResponses) {
        this.indexCardResponses = indexCardResponses;
    }
}
