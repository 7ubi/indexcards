package com.x7ubi.indexcards.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "INDEXCARDASSESSMENT")
public class IndexCardAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "indexcard_assessment_id", nullable = false, updatable = false)
    private Long indexcardAssessmentId;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Assessment assessment;

    @Column(nullable = false)
    private LocalDateTime date;

    public IndexCardAssessment() {}

    public IndexCardAssessment(Assessment assessment, LocalDateTime date) {
        this.assessment = assessment;
        this.date = date;
    }

    public Long getIndexcardAssessmentId() {
        return indexcardAssessmentId;
    }

    public void setIndexcardAssessmentId(Long indexcardAssessmentId) {
        this.indexcardAssessmentId = indexcardAssessmentId;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
