package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EditProjectServiceTest extends ProjectTestConfig {

    @Test
    public void editProjectTest() throws EntityNotFoundException, EntityCreationException, UnauthorizedException {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest("edited project");

        // when
        this.editProjectService.editProject(createProjectRequest, project.getId(), user.getUsername());

        // then
        project = this.projectRepo.findProjectByName(createProjectRequest.getName()).get(0);
        Assertions.assertEquals(project.getName(), createProjectRequest.getName());
    }

    @Test
    public void editProjectSetsExamDateTest() throws EntityNotFoundException, EntityCreationException, UnauthorizedException {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest("edited project");
        createProjectRequest.setExamDate(LocalDate.now().plusDays(14));

        // when
        this.editProjectService.editProject(createProjectRequest, project.getId(), user.getUsername());

        // then
        project = this.projectRepo.findProjectByName(createProjectRequest.getName()).get(0);
        Assertions.assertEquals(createProjectRequest.getExamDate(), project.getExamDate());
    }

    @Test
    public void editProjectClearsExamDateTest() throws EntityNotFoundException, EntityCreationException, UnauthorizedException {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        project.setExamDate(LocalDate.now().plusDays(14));
        this.projectRepo.save(project);

        CreateProjectRequest createProjectRequest = new CreateProjectRequest("edited project");
        createProjectRequest.setExamDate(null);

        // when
        this.editProjectService.editProject(createProjectRequest, project.getId(), user.getUsername());

        // then
        project = this.projectRepo.findProjectByName(createProjectRequest.getName()).get(0);
        Assertions.assertNull(project.getExamDate());
    }

    @Test
    public void editProjectKeepingSameNameSucceedsTest()
            throws EntityNotFoundException, EntityCreationException, UnauthorizedException {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest(this.projects.get(0).getName());
        createProjectRequest.setExamDate(LocalDate.now().plusDays(14));

        // when
        this.editProjectService.editProject(createProjectRequest, project.getId(), user.getUsername());

        // then
        project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        Assertions.assertEquals(createProjectRequest.getExamDate(), project.getExamDate());
    }

    @Test
    public void editProjectWithExamDateCompressesCardDueDatesTest()
            throws EntityNotFoundException, EntityCreationException, UnauthorizedException {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        IndexCard indexCard = new IndexCard();
        indexCard.setQuestion(StandardCharsets.UTF_8.encode("Question").array());
        indexCard.setAnswer(StandardCharsets.UTF_8.encode("Answer").array());
        indexCard.setProject(project);
        indexCard.setDueDate(LocalDateTime.now().plusDays(30));
        indexCard = this.indexCardRepo.save(indexCard);

        CreateProjectRequest createProjectRequest = new CreateProjectRequest(project.getName());
        createProjectRequest.setExamDate(LocalDate.now().plusDays(4));

        // when
        this.editProjectService.editProject(createProjectRequest, project.getId(), user.getUsername());

        // then
        // daysUntilExam=4 caps the interval at 4/2=2, pulling the far-future due date forward
        IndexCard updatedIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(indexCard.getId());
        Assertions.assertEquals(
                LocalDateTime.now().plusDays(2).getDayOfYear(), updatedIndexCard.getDueDate().getDayOfYear());
    }

    @Test
    public void editProjectWithoutExamDateLeavesCardDueDatesUntouchedTest()
            throws EntityNotFoundException, EntityCreationException, UnauthorizedException {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        LocalDateTime farFutureDueDate = LocalDateTime.now().plusDays(30);

        IndexCard indexCard = new IndexCard();
        indexCard.setQuestion(StandardCharsets.UTF_8.encode("Question").array());
        indexCard.setAnswer(StandardCharsets.UTF_8.encode("Answer").array());
        indexCard.setProject(project);
        indexCard.setDueDate(farFutureDueDate);
        indexCard = this.indexCardRepo.save(indexCard);

        CreateProjectRequest createProjectRequest = new CreateProjectRequest("edited project");

        // when
        this.editProjectService.editProject(createProjectRequest, project.getId(), user.getUsername());

        // then
        IndexCard updatedIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(indexCard.getId());
        Assertions.assertEquals(farFutureDueDate.getDayOfYear(), updatedIndexCard.getDueDate().getDayOfYear());
    }

    @Test
    public void editProjectWithNonexistentUser() {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject1");

        // when
        EntityNotFoundException entityNotFoundException = Assert.assertThrows(EntityNotFoundException.class, () ->
                this.editProjectService.editProject(createProjectRequest, project.getId(), "nonexistent"));

        // then
        Assertions.assertEquals(entityNotFoundException.getMessage(), ErrorMessage.Project.USERNAME_NOT_FOUND);
        Assertions.assertTrue(this.projectRepo.findProjectByName(createProjectRequest.getName()).isEmpty());
        Assertions.assertFalse(this.projectRepo.findProjectByName(this.projects.get(0).getName()).isEmpty());
    }

    @Test
    public void editProjectNameExistsTest() throws EntityNotFoundException, EntityCreationException {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject1");

        // when
        this.createProjectService.createProject("test", createProjectRequest);
        EntityCreationException entityCreationException = Assert.assertThrows(EntityCreationException.class, () ->
                this.editProjectService.editProject(createProjectRequest, project.getId(), "test"));

        // then
        Assertions.assertEquals(entityCreationException.getMessage(), ErrorMessage.Project.PROJECT_NAME_EXISTS);
        Assertions.assertFalse(this.projectRepo.findProjectByName(createProjectRequest.getName()).isEmpty());
        Assertions.assertFalse(this.projectRepo.findProjectByName(this.projects.get(0).getName()).isEmpty());
    }

    @Test
    public void editProjectWithTooLongName() {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        String projectName = new String(new char[101]).replace('\0', 'A');
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName(projectName);

        // when
        EntityCreationException entityCreationException = Assert.assertThrows(EntityCreationException.class, () ->
                this.editProjectService.editProject(createProjectRequest, project.getId(), "test"));
        // then
        Assertions.assertEquals(entityCreationException.getMessage(), ErrorMessage.Project.PROJECT_NAME_TOO_LONG);
        Assertions.assertFalse(this.projectRepo.findProjectByName(project.getName()).isEmpty());
        Assertions.assertFalse(this.projectRepo.findProjectByName(this.projects.get(0).getName()).isEmpty());
    }

    @Test
    public void editProjectWrongUsername() {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("Edit Project");

        // when
        UnauthorizedException unauthorizedException = Assert.assertThrows(UnauthorizedException.class, () ->
                this.editProjectService.editProject(createProjectRequest, project.getId(), "test2"));

        // then
        Assertions.assertEquals(unauthorizedException.getMessage(), ErrorMessage.Project.USER_NOT_PROJECT_OWNER);
        Assertions.assertTrue(this.projectRepo.findProjectByName(createProjectRequest.getName()).isEmpty());
        Assertions.assertFalse(this.projectRepo.findProjectByName(this.projects.get(0).getName()).isEmpty());
    }
}
