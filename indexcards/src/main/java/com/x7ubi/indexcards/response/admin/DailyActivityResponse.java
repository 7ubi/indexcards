package com.x7ubi.indexcards.response.admin;

import java.time.LocalDate;

public class DailyActivityResponse {

    private LocalDate date;

    private long assessments;

    private long activeUsers;

    public DailyActivityResponse() {}

    public DailyActivityResponse(LocalDate date, long assessments, long activeUsers) {
        this.date = date;
        this.assessments = assessments;
        this.activeUsers = activeUsers;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getAssessments() {
        return assessments;
    }

    public void setAssessments(long assessments) {
        this.assessments = assessments;
    }

    public long getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(long activeUsers) {
        this.activeUsers = activeUsers;
    }
}
