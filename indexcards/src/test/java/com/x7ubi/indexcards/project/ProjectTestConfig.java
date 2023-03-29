package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.service.project.CreateProjectService;
import com.x7ubi.indexcards.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

public abstract class ProjectTestConfig {
    @Autowired
    protected UserRepo userRepo;

    @Autowired
    protected ProjectRepo projectRepo;

    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected CreateProjectService createProjectService;

    @Autowired
    protected ProjectService projectService;

    protected User user;

    protected User user2;

    protected static final String WRONGFULLY_UNSUCCESSFUL = "Success was expected!";

    protected static final String WRONGFULLY_SUCCESSFUL = "Failure was expected!";

    protected static final String WRONG_NUMBER_OF_ERRORS = "A different number of Errors was expected";

    @BeforeEach
    void projectTestSetup() {
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
    }
}
