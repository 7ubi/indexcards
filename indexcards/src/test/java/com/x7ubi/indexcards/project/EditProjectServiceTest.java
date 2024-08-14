package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
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
    public void editProjectTest() throws EntityNotFoundException, EntityCreationException {
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
}
