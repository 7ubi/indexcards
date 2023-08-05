package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.response.common.ResultResponse;
import com.x7ubi.indexcards.response.project.UserProjectResponse;
import com.x7ubi.indexcards.response.project.UserProjectsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProjectServiceTest extends ProjectTestConfig {

    private ArrayList<Project> projects;

    private IndexCard indexCard;

    @Autowired
    private IndexCardRepo indexCardRepo;

    @BeforeEach
    public void createProjects() {
        this.projects = new ArrayList<>();
        this.projects.add(new Project("TestProject", null));
        this.projectRepo.save(this.projects.get(0));

        User userToEdit = this.userRepo.findByUsername(this.user.getUsername()).get();
        userToEdit.setProjects(projects);
        this.userRepo.save(userToEdit);
    }

    private void createIndexCardsForProject() {
        this.indexCard = new IndexCard();
        this.indexCard.setQuestion("Question");
        this.indexCard.setAnswer("Answer");

        this.indexCardRepo.save(this.indexCard);
        Set<IndexCard> indexCards = new HashSet<>();
        this.indexCard = this.indexCardRepo.findIndexCardByQuestion(this.indexCard.getQuestion());
        indexCards.add(this.indexCard);
        this.projects.get(0).setIndexCards(indexCards);
        this.projectRepo.save(this.projects.get(0));
    }

    @Test
    public void getAllUserProjectsTest() {
        // when
        UserProjectsResponse userProjectsResponse = this.projectService.getUserProjects(user.getUsername());

        // then
        assertEquals(WRONGFULLY_UNSUCCESSFUL, userProjectsResponse.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, userProjectsResponse.getErrorMessages().isEmpty(), true);
        assertThat(userProjectsResponse.getProjectResponses().size()).isEqualTo(1);
        assertThat(userProjectsResponse.getProjectResponses().get(0).getName())
                .isEqualTo(this.projects.get(0).getName());
    }

    @Test
    public void getAllUserProjectsWithWrongUsernameTest() {
        // when
        UserProjectsResponse userProjectsResponse = this.projectService.getUserProjects("wrong username");

        // then
        assertEquals(WRONGFULLY_UNSUCCESSFUL, userProjectsResponse.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, userProjectsResponse.getErrorMessages().size(), 1);
        assertThat(userProjectsResponse.getErrorMessages().get(0).getMessage())
                .isEqualTo(ErrorMessage.Project.USERNAME_NOT_FOUND);
        assertThat(userProjectsResponse.getProjectResponses()).isEqualTo(null);
    }

    @Test
    public void getUserProjectTest() {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        // when
        UserProjectResponse userProjectsResponse = this.projectService.getProject(project.getId());

        // then
        assertEquals(WRONGFULLY_UNSUCCESSFUL, userProjectsResponse.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, userProjectsResponse.getErrorMessages().isEmpty(), true);
        assertThat(userProjectsResponse.getProjectResponse().getName()).isEqualTo(this.projects.get(0).getName());
    }

    @Test
    public void getUserProjectWithWrongProjectIdTest() {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        // when
        UserProjectResponse userProjectResponse = this.projectService.getProject(project.getId() + 1);

        // then
        assertEquals(WRONGFULLY_SUCCESSFUL, userProjectResponse.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, userProjectResponse.getErrorMessages().size(), 1);
        assertEquals(WRONG_NUMBER_OF_ERRORS, userProjectResponse.getErrorMessages().get(0).getMessage(),
                ErrorMessage.Project.PROJECT_NOT_FOUND);
        assertThat(userProjectResponse.getProjectResponse()).isEqualTo(null);
    }

    @Test
    public void deleteProjectTest() {
        // given
        createIndexCardsForProject();
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        //when
        ResultResponse resultResponse = this.projectService.deleteProject(this.user.getUsername(), project.getId());

        //then
        project = this.projectRepo.findProjectByProjectId(project.getId());
        user = this.userRepo.findByUsername(this.user.getUsername()).get();
        indexCard = this.indexCardRepo.findIndexCardByIndexcardId(indexCard.getId());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, resultResponse.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, resultResponse.getErrorMessages().isEmpty(), true);
        assertThat(user.getProjects().isEmpty()).isTrue();
        assertThat(project).isEqualTo(null);
        assertThat(indexCard).isEqualTo(null);
    }

    @Test
    public void deleteProjectWithWrongProjectIdTest() {
        // given
        createIndexCardsForProject();
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        //when
        ResultResponse resultResponse
                = this.projectService.deleteProject(this.user.getUsername(), project.getId() + 1);

        //then
        project = this.projectRepo.findProjectByProjectId(project.getId());
        user = this.userRepo.findByUsername(this.user.getUsername()).get();
        indexCard = this.indexCardRepo.findIndexCardByIndexcardId(indexCard.getId());
        assertEquals(WRONGFULLY_SUCCESSFUL, resultResponse.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, resultResponse.getErrorMessages().isEmpty(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, resultResponse.getErrorMessages().size(), 1);
        assertThat(resultResponse.getErrorMessages().get(0).getMessage())
                .isEqualTo(ErrorMessage.Project.PROJECT_NOT_FOUND);
        assertThat(user.getProjects().size()).isEqualTo(1);
        assertThat(project).isNotEqualTo(null);
        assertThat(indexCard).isNotEqualTo(null);
    }
}
