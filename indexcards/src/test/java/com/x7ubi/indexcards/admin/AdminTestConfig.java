package com.x7ubi.indexcards.admin;

import com.x7ubi.indexcards.TestConfig;
import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.IndexCardAssessment;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=USER"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public abstract class AdminTestConfig extends TestConfig {

    @Autowired
    protected UserRepo userRepo;

    @Autowired
    protected ProjectRepo projectRepo;

    @Autowired
    protected IndexCardRepo indexCardRepo;

    @Autowired
    protected IndexCardAssessmentRepo indexCardAssessmentRepo;

    protected User admin;

    protected User user;

    @BeforeEach
    void setupAdmin() {
        this.indexCardRepo.deleteAll();
        this.projectRepo.deleteAll();
        this.userRepo.deleteAll();

        this.admin = saveUser("admin", Role.ADMIN, LocalDateTime.now());
        this.user = saveUser("test", Role.USER, null);
    }

    protected User saveUser(String username, Role role, LocalDateTime createdAt) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setFirstname("Max");
        newUser.setSurname("Muster");
        newUser.setProjects(new ArrayList<>());
        newUser.setPassword("1234");
        newUser.setRole(role);
        newUser.setCreatedAt(createdAt);
        return this.userRepo.save(newUser);
    }

    protected Project saveProject(String name, User owner, boolean archived) {
        Project newProject = new Project(name, null);
        newProject.setUser(owner);
        newProject.setArchived(archived);
        return this.projectRepo.save(newProject);
    }

    protected IndexCard saveIndexCard(Project owningProject) {
        IndexCard newIndexCard = new IndexCard(
                "Question".getBytes(StandardCharsets.UTF_8), "Answer".getBytes(StandardCharsets.UTF_8));
        newIndexCard.setProject(owningProject);
        return this.indexCardRepo.save(newIndexCard);
    }

    protected void assess(IndexCard card, Assessment assessment, LocalDateTime date) {
        IndexCardAssessment indexCardAssessment = new IndexCardAssessment(assessment, date);
        this.indexCardAssessmentRepo.save(indexCardAssessment);
        IndexCard managedCard = this.indexCardRepo.findIndexCardByIndexcardId(card.getId());
        managedCard.setAssessment(assessment);
        managedCard.getAssessmentHistory().add(indexCardAssessment);
        this.indexCardRepo.save(managedCard);
    }
}
