package com.x7ubi.indexcards.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "INDEXCARDASSESSMENT")
public class IndexCardAssessment {
    @Id
    // All entities share the hibernate_sequence table (incremented by 1) that Hibernate 5 created; without this,
    // Hibernate 6+ would expect a separate <entity>_seq table per entity.
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "hibernate_sequence", allocationSize = 1)
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
