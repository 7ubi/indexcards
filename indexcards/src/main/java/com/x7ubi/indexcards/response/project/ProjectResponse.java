package com.x7ubi.indexcards.response.project;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;

import java.time.LocalDate;
import java.util.List;

public class ProjectResponse {

    private Long id;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDate examDate;

    private List<IndexCardResponse> indexCardResponses;

    public ProjectResponse() {}

    public ProjectResponse(Long id, String name, List<IndexCardResponse> indexCardResponses) {
        this.id = id;
        this.name = name;
        this.indexCardResponses = indexCardResponses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public List<IndexCardResponse> getIndexCardResponses() {
        return indexCardResponses;
    }

    public void setIndexCardResponses(List<IndexCardResponse> indexCardResponses) {
        this.indexCardResponses = indexCardResponses;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
