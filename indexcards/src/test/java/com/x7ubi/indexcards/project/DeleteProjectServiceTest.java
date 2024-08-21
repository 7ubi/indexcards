package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DeleteProjectServiceTest extends ProjectTestConfig {

    private IndexCard indexCard;

    @Autowired
    private IndexCardRepo indexCardRepo;


    private void createIndexCardsForProject() {
        this.indexCard = new IndexCard();
        this.indexCard.setQuestion(StandardCharsets.UTF_8.encode("Question").array());
        this.indexCard.setAnswer(StandardCharsets.UTF_8.encode("Answer").array());

        this.indexCardRepo.save(this.indexCard);
        Set<IndexCard> indexCards = new HashSet<>();
        this.indexCard = this.indexCardRepo.findIndexCardByQuestion(this.indexCard.getQuestion());
        indexCards.add(this.indexCard);
        this.projects.get(0).setIndexCards(indexCards);
        this.projectRepo.save(this.projects.get(0));
    }

    @Test
    public void deleteProjectTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        createIndexCardsForProject();
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        //when
        this.deleteProjectService.deleteProject(this.user.getUsername(), project.getId());

        //then
        project = this.projectRepo.findProjectByProjectId(project.getId());
        user = this.userRepo.findByUsername(this.user.getUsername()).orElse(null);
        indexCard = this.indexCardRepo.findIndexCardByIndexcardId(indexCard.getId());
        Assertions.assertTrue(user.getProjects().isEmpty());
        Assertions.assertNull(project);
        Assertions.assertNull(indexCard);
    }

    @Test
    public void deleteProjectWithWrongProjectIdTest() {
        // given
        createIndexCardsForProject();
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        //when
        EntityNotFoundException entityNotFoundException = Assert.assertThrows(EntityNotFoundException.class, () ->
                this.deleteProjectService.deleteProject(this.user.getUsername(), project.getId() + 1));

        //then
        user = this.userRepo.findByUsername(this.user.getUsername()).orElse(null);
        Assertions.assertEquals(entityNotFoundException.getMessage(), ErrorMessage.Project.PROJECT_NOT_FOUND);
        Assertions.assertEquals(user.getProjects().size(), 1);
        Assertions.assertTrue(this.projectRepo.existsByProjectId(project.getId()));
        Assertions.assertTrue(this.indexCardRepo.existsIndexCardByIndexcardId(indexCard.getId()));
    }

    @Test
    public void deleteProjectWithWrongUsernameTest() {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        //when
        UnauthorizedException unauthorizedException = Assert.assertThrows(UnauthorizedException.class, () ->
                this.deleteProjectService.deleteProject("test2", project.getId()));

        //then
        user = this.userRepo.findByUsername(this.user.getUsername()).orElse(null);
        Assertions.assertEquals(unauthorizedException.getMessage(), ErrorMessage.Project.USER_NOT_PROJECT_OWNER);
        Assertions.assertEquals(user.getProjects().size(), 1);
        Assertions.assertTrue(this.projectRepo.existsByProjectId(project.getId()));
    }
}
