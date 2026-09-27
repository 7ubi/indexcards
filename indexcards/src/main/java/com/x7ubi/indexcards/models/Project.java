package com.x7ubi.indexcards.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "PROJECT")
public class Project {
    @Id
    // All entities share the hibernate_sequence table (incremented by 1) that Hibernate 5 created; without this,
    // Hibernate 6+ would expect a separate <entity>_seq table per entity.
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "project_id", nullable = false, updatable = false)
    private Long projectId;

    @Column(nullable = false, length = 100)
    private String name;

    private LocalDate examDate;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "project")
    private Set<IndexCard> indexCards;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    public Project() {
    }

    public Project(String name, Set<IndexCard> indexCards) {
        this.name = name;
        this.indexCards = indexCards;
    }

    public Long getId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public Set<IndexCard> getIndexCards() {
        return indexCards;
    }

    public void setIndexCards(Set<IndexCard> indexCards) {
        this.indexCards = indexCards;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
