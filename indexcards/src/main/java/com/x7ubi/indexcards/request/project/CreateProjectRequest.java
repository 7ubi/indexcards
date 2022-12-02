package com.x7ubi.indexcards.request.project;

public class CreateProjectRequest {
    private String name;

    public CreateProjectRequest() {}

    public CreateProjectRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
