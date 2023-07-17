package com.x7ubi.indexcards.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "INDEXCARDS")
public class IndexCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "indexcard_id", nullable = false, updatable = false)
    private Long indexcardId;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, length = 500)
    private String answer;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Assessment assessment = Assessment.UNRATED;

    @OneToMany(cascade=CascadeType.REMOVE, fetch = FetchType.EAGER)
    private Set<IndexCardAssessment> assessmentHistory = new HashSet<>();

    public IndexCard(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public IndexCard() {}

    public Long getId() {
        return indexcardId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
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
}
