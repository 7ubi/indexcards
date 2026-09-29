package com.x7ubi.indexcards.response.admin;

public class AnalyticsTotalsResponse {

    private long users;

    private long activeProjects;

    private long archivedProjects;

    private long indexCards;

    private long assessments;

    public AnalyticsTotalsResponse() {}

    public AnalyticsTotalsResponse(long users, long activeProjects, long archivedProjects, long indexCards, long assessments) {
        this.users = users;
        this.activeProjects = activeProjects;
        this.archivedProjects = archivedProjects;
        this.indexCards = indexCards;
        this.assessments = assessments;
    }

    public long getUsers() {
        return users;
    }

    public void setUsers(long users) {
        this.users = users;
    }

    public long getActiveProjects() {
        return activeProjects;
    }

    public void setActiveProjects(long activeProjects) {
        this.activeProjects = activeProjects;
    }

    public long getArchivedProjects() {
        return archivedProjects;
    }

    public void setArchivedProjects(long archivedProjects) {
        this.archivedProjects = archivedProjects;
    }

    public long getIndexCards() {
        return indexCards;
    }

    public void setIndexCards(long indexCards) {
        this.indexCards = indexCards;
    }

    public long getAssessments() {
        return assessments;
    }

    public void setAssessments(long assessments) {
        this.assessments = assessments;
    }
}
