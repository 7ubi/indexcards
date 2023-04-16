package com.x7ubi.indexcards.request.indexcard;

import com.x7ubi.indexcards.models.Assessment;

public class AssessmentRequest {
    private Long indexCardId;

    private Assessment assessment;

    public Long getIndexCardId() {
        return indexCardId;
    }

    public void setIndexCardId(Long indexCardId) {
        this.indexCardId = indexCardId;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }
}
