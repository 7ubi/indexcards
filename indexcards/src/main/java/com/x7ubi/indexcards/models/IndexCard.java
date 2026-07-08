package com.x7ubi.indexcards.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "INDEXCARDS")
public class IndexCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "indexcard_id", nullable = false, updatable = false)
    private Long indexcardId;

    @Column(nullable = false)
    @Lob
    private byte[] question;

    @Column(nullable = false, length = 500)
    @Lob
    private byte[] answer;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Assessment assessment = Assessment.UNRATED;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<IndexCardAssessment> assessmentHistory = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "project_id")
    private Project project;

    private Double easeFactor;

    private Integer repetitions;

    private Integer intervalDays;

    private LocalDateTime dueDate;

    public IndexCard(byte[] question, byte[] answer) {
        this.question = question;
        this.answer = answer;
    }

    public IndexCard() {
    }

    public Long getId() {
        return indexcardId;
    }

    public byte[] getQuestion() {
        return question;
    }

    public void setQuestion(byte[] question) {
        this.question = question;
    }

    public byte[] getAnswer() {
        return answer;
    }

    public void setAnswer(byte[] answer) {
        this.answer = answer;
    }

    public Assessment getAssessment() {
        return assessment;
    }

    public void setAssessment(Assessment assessment) {
        this.assessment = assessment;
    }

    public Long getIndexcardId() {
        return indexcardId;
    }

    public void setIndexcardId(Long indexcardId) {
        this.indexcardId = indexcardId;
    }

    public Set<IndexCardAssessment> getAssessmentHistory() {
        return assessmentHistory;
    }

    public void setAssessmentHistory(Set<IndexCardAssessment> assessmentHistory) {
        this.assessmentHistory = assessmentHistory;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * Cards persisted before scheduling was introduced (or otherwise never scheduled) have a
     * {@code NULL} column here; treat them like a fresh card with the base ease factor.
     */
    public Double getEaseFactor() {
        return easeFactor != null ? easeFactor : 2.5;
    }

    public void setEaseFactor(Double easeFactor) {
        this.easeFactor = easeFactor;
    }

    public Integer getRepetitions() {
        return repetitions != null ? repetitions : 0;
    }

    public void setRepetitions(Integer repetitions) {
        this.repetitions = repetitions;
    }

    public Integer getIntervalDays() {
        return intervalDays != null ? intervalDays : 0;
    }

    public void setIntervalDays(Integer intervalDays) {
        this.intervalDays = intervalDays;
    }

    /**
     * Never-scheduled cards (new or legacy, {@code NULL} column) are always immediately due.
     */
    public LocalDateTime getDueDate() {
        return dueDate != null ? dueDate : LocalDateTime.now();
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }
}
