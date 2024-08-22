package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.TestConfig;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.service.indexcard.CreateIndexCardService;
import com.x7ubi.indexcards.service.indexcard.DeleteIndexCardService;
import com.x7ubi.indexcards.service.indexcard.EditIndexCardService;
import com.x7ubi.indexcards.service.indexcard.IndexCardAssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;

public abstract class IndexCardTestConfig extends TestConfig {
    @Autowired
    protected UserRepo userRepo;

    @Autowired
    protected ProjectRepo projectRepo;

    @Autowired
    protected IndexCardRepo indexCardRepo;

    @Autowired
    protected CreateIndexCardService createIndexCardService;

    @Autowired
    protected IndexCardAssessmentService indexCardAssessmentService;

    @Autowired
    protected DeleteIndexCardService deleteIndexCardService;

    @Autowired
    protected EditIndexCardService editIndexCardService;

    protected User user;

    protected User user2;

    protected ArrayList<Project> projects;

    protected IndexCard indexCard;

    @BeforeEach
    void indexCardTestSetup() {
        this.user = new User();
        this.user.setUsername("test");
        this.user.setFirstname("Max");
        this.user.setSurname("Muster");
        this.user.setProjects(new ArrayList<>());
        this.user.setPassword("1234");

        this.user = this.userRepo.save(this.user);

        this.user2 = new User();
        this.user2.setUsername("test2");
        this.user2.setFirstname("Max");
        this.user2.setSurname("Muster");
        this.user2.setProjects(new ArrayList<>());
        this.user2.setPassword("1234");

        this.userRepo.save(this.user2);

        this.projects = new ArrayList<>();
        Project project = new Project("TestProject", null);
        project.setUser(this.user);
        project.setIndexCards(new HashSet<>());
        project = this.projectRepo.save(project);
        this.projects.add(project);

        User userToEdit = this.userRepo.findByUsername(this.user.getUsername()).orElse(null);
        assert userToEdit != null;
        userToEdit.setProjects(projects);
    }

    protected void createIndexCard() {
        this.indexCard = new IndexCard();
        this.indexCard.setQuestion(StandardCharsets.UTF_8.encode("Question").array());
        this.indexCard.setAnswer(StandardCharsets.UTF_8.encode("Answer").array());

        this.indexCard.setProject(this.projects.get(0));

        this.indexCard = this.indexCardRepo.save(this.indexCard);
    }
}
