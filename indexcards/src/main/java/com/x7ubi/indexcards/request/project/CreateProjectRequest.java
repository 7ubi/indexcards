package com.x7ubi.indexcards.request.project;

import java.time.LocalDate;

public class CreateProjectRequest {
    private String name;

    private LocalDate examDate;

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

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }
}
