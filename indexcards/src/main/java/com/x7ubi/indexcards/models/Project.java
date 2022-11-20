package com.x7ubi.indexcards.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "PROJECT")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false, updatable = false)
    private Long project_id;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany()
    private Set<IndexCard> indexCards;

    public Project() {}

    public Project(String name, Set<IndexCard> indexCards) {
        this.name = name;
        this.indexCards = indexCards;
    }

    public Long getId() {
        return project_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<IndexCard> getIndexCards() {
        return indexCards;
    }

    public void setIndexCards(Set<IndexCard> indexCards) {
        this.indexCards = indexCards;
    }
}
