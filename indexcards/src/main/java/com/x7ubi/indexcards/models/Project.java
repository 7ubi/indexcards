package com.x7ubi.indexcards.models;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "PROJECT")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "project_id", nullable = false, updatable = false)
    private Long projectId;

    @Column(nullable = false, length = 100)
    private String name;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
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
