package com.x7ubi.indexcards.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "USER")
public class User implements Serializable {
    @Id
    // All entities share the hibernate_sequence table (incremented by 1) that Hibernate 5 created; without this,
    // Hibernate 6+ would expect a separate <entity>_seq table per entity.
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = "hibernate_sequence", allocationSize = 1)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 100)
    private String firstname;

    @Column(nullable = false, length = 100)
    private String surname;

    @Column(nullable = false, length = 100)
    private String password;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Project> projects;

    public User() {
    }

    public User(String username, String firstname, String surname, String password, List<Project> projects) {
        this.username = username;
        this.firstname = firstname;
        this.surname = surname;
        this.password = password;
        this.projects = projects;
    }

    public Long getId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
