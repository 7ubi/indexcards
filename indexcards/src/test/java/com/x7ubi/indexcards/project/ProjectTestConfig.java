package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.service.project.CreateProjectService;
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


    @BeforeEach
    void projectTestSetup() {
        User user = new User();
        user.setUsername("test");
        user.setFirstname("Max");
        user.setSurname("Muster");
        user.setProjects(new ArrayList<>());
        user.setPassword(passwordEncoder.encode("1234"));

        this.userRepo.save(user);
    }

}
