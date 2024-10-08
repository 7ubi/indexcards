package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.TestConfig;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.service.project.CreateProjectService;
import com.x7ubi.indexcards.service.project.DeleteProjectService;
import com.x7ubi.indexcards.service.project.EditProjectService;
import com.x7ubi.indexcards.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class ProjectTestConfig extends TestConfig {
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

    @Autowired
    protected DeleteProjectService deleteProjectService;

    @Autowired
    protected EditProjectService editProjectService;

    protected User user;

    protected User user2;

    protected ArrayList<Project> projects;

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

        this.projects = new ArrayList<>();
        this.projects.add(new Project("TestProject", null));
        projects.get(0).setUser(this.user);
        this.projectRepo.save(this.projects.get(0));

        User userToEdit = this.userRepo.findByUsername(this.user.getUsername()).orElse(null);
        assert userToEdit != null;
        userToEdit.setProjects(projects);
        this.userRepo.save(userToEdit);
    }
}
