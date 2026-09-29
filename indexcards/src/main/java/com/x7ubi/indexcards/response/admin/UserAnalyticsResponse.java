package com.x7ubi.indexcards.response.admin;

import java.time.LocalDateTime;

public class UserAnalyticsResponse {

    private String username;

    private LocalDateTime createdAt;

    private long projects;

    private long indexCards;

    private long assessments;

    private LocalDateTime lastActivity;

    private boolean admin;

    public UserAnalyticsResponse() {}

    public UserAnalyticsResponse(String username, LocalDateTime createdAt, long projects, long indexCards, long assessments, LocalDateTime lastActivity, boolean admin) {
        this.username = username;
        this.createdAt = createdAt;
        this.projects = projects;
        this.indexCards = indexCards;
        this.assessments = assessments;
        this.lastActivity = lastActivity;
        this.admin = admin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getProjects() {
        return projects;
    }

    public void setProjects(long projects) {
        this.projects = projects;
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

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
