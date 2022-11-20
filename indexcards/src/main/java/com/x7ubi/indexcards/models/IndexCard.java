package com.x7ubi.indexcards.models;

import javax.persistence.*;

@Entity
@Table(name = "INDEXCARDS")
public class IndexCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long indexcard_id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, length = 500)
    private String answer;

    public IndexCard(String name, String question, String answer) {
        this.name = name;
        this.question = question;
        this.answer = answer;
    }

    public IndexCard() {

    }

    public Long getId() {
        return indexcard_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
