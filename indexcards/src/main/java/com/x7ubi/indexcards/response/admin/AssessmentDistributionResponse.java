package com.x7ubi.indexcards.response.admin;

public class AssessmentDistributionResponse {

    private long unrated;

    private long bad;

    private long ok;

    private long good;

    public AssessmentDistributionResponse() {}

    public AssessmentDistributionResponse(long unrated, long bad, long ok, long good) {
        this.unrated = unrated;
        this.bad = bad;
        this.ok = ok;
        this.good = good;
    }

    public long getUnrated() {
        return unrated;
    }

    public void setUnrated(long unrated) {
        this.unrated = unrated;
    }

    public long getBad() {
        return bad;
    }

    public void setBad(long bad) {
        this.bad = bad;
    }

    public long getOk() {
        return ok;
    }

    public void setOk(long ok) {
        this.ok = ok;
    }

    public long getGood() {
        return good;
    }

    public void setGood(long good) {
        this.good = good;
    }
}
