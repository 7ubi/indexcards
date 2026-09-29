package com.x7ubi.indexcards.response.admin;

import java.util.List;

public class AnalyticsResponse {

    private AnalyticsTotalsResponse totals;

    private AssessmentDistributionResponse assessmentDistribution;

    private List<DailyActivityResponse> dailyActivity;

    private List<DailyCountResponse> dailySignups;

    private List<UserAnalyticsResponse> users;

    public AnalyticsResponse() {}

    public AnalyticsResponse(AnalyticsTotalsResponse totals, AssessmentDistributionResponse assessmentDistribution, List<DailyActivityResponse> dailyActivity, List<DailyCountResponse> dailySignups, List<UserAnalyticsResponse> users) {
        this.totals = totals;
        this.assessmentDistribution = assessmentDistribution;
        this.dailyActivity = dailyActivity;
        this.dailySignups = dailySignups;
        this.users = users;
    }

    public AnalyticsTotalsResponse getTotals() {
        return totals;
    }

    public void setTotals(AnalyticsTotalsResponse totals) {
        this.totals = totals;
    }

    public AssessmentDistributionResponse getAssessmentDistribution() {
        return assessmentDistribution;
    }

    public void setAssessmentDistribution(AssessmentDistributionResponse assessmentDistribution) {
        this.assessmentDistribution = assessmentDistribution;
    }

    public List<DailyActivityResponse> getDailyActivity() {
        return dailyActivity;
    }

    public void setDailyActivity(List<DailyActivityResponse> dailyActivity) {
        this.dailyActivity = dailyActivity;
    }

    public List<DailyCountResponse> getDailySignups() {
        return dailySignups;
    }

    public void setDailySignups(List<DailyCountResponse> dailySignups) {
        this.dailySignups = dailySignups;
    }

    public List<UserAnalyticsResponse> getUsers() {
        return users;
    }

    public void setUsers(List<UserAnalyticsResponse> users) {
        this.users = users;
    }
}
