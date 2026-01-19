package com.x7ubi.indexcards.request.indexcard;

public class IndexCardCsvImportRequest {

    private String csv;

    private Long projectId;

    public String getCsv() {
        return csv;
    }

    public void setCsv(String csv) {
        this.csv = csv;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
