package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.TestConfig;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.service.indexcard.CreateIndexCardService;
import com.x7ubi.indexcards.service.indexcard.IndexCardAssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

public abstract class IndexCardTestConfig extends TestConfig {
    @Autowired
    protected UserRepo userRepo;

    @Autowired
    protected ProjectRepo projectRepo;

    @Autowired
    protected IndexCardRepo indexCardRepo;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected CreateIndexCardService createIndexCardService;

    @Autowired
    protected IndexCardAssessmentService indexCardAssessmentService;

    protected User user;

    protected User user2;

    protected ArrayList<Project> projects;

    @BeforeEach
    void indexCardTestSetup() {
        this.user = new User();
        this.user.setUsername("test");
        this.user.setFirstname("Max");
        this.user.setSurname("Muster");
        this.user.setProjects(new ArrayList<>());
        this.user.setPassword(this.passwordEncoder.encode("1234"));

        this.userRepo.save(this.user);

        this.user2 = new User();
        this.user2.setUsername("test2");
        this.user2.setFirstname("Max");
        this.user2.setSurname("Muster");
        this.user2.setProjects(new ArrayList<>());
        this.user2.setPassword(this.passwordEncoder.encode("1234"));

        this.userRepo.save(this.user2);

        this.projects = new ArrayList<>();
        this.projects.add(new Project("TestProject", null));
        this.projectRepo.save(this.projects.get(0));

        User userToEdit = this.userRepo.findByUsername(this.user.getUsername()).get();
        userToEdit.setProjects(projects);
    }
}
