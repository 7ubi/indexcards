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

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CreateProjectServiceTest extends ProjectTestConfig {

    @Test
    public void createProjectTest() throws EntityNotFoundException, EntityCreationException {
        // given
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject1");

        // when
        this.createProjectService.createProject("test", createProjectRequest);

        // then
        Project project = this.projectRepo.findProjectByName(createProjectRequest.getName()).get(0);
        Assertions.assertEquals(createProjectRequest.getName(), project.getName());
    }

    @Test
    public void createProjectWithNonexistentUser() {
        // given
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject1");

        // when
        EntityNotFoundException entityNotFoundException = Assert.assertThrows(EntityNotFoundException.class, () ->
                this.createProjectService.createProject("nonexistent", createProjectRequest));

        // then
        Assertions.assertEquals(entityNotFoundException.getMessage(), ErrorMessage.Project.USERNAME_NOT_FOUND);
    }

    @Test
    public void createProjectTwiceTest() throws EntityNotFoundException, EntityCreationException {
        // given
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject1");

        // when
        this.createProjectService.createProject("test", createProjectRequest);
        EntityCreationException entityCreationException = Assert.assertThrows(EntityCreationException.class, () ->
                this.createProjectService.createProject("test", createProjectRequest));

        // then
        Project project = this.projectRepo.findProjectByName(createProjectRequest.getName()).get(0);
        Assertions.assertEquals(createProjectRequest.getName(), project.getName());
        Assertions.assertEquals(ErrorMessage.Project.PROJECT_NAME_EXISTS, entityCreationException.getMessage());
    }

    @Test
    public void createProjectTwiceFromDifferentUserTest() throws EntityNotFoundException, EntityCreationException {
        // given
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject1");

        // when
        this.createProjectService.createProject("test", createProjectRequest);
        this.createProjectService.createProject("test2", createProjectRequest);

        // then
        List<Project> project = this.projectRepo.findProjectByName(createProjectRequest.getName());
        Assertions.assertEquals(createProjectRequest.getName(), project.get(0).getName());
        Assertions.assertEquals(createProjectRequest.getName(), project.get(1).getName());
    }

    @Test
    public void createProjectWithTooLongName() {
        // given
        String projectName = new String(new char[101]).replace('\0', 'A');
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName(projectName);

        // when
        EntityCreationException entityCreationException = Assert.assertThrows(EntityCreationException.class, () ->
                this.createProjectService.createProject("test", createProjectRequest));

        // then
        List<Project> project = this.projectRepo.findProjectByName(projectName);
        Assertions.assertEquals(ErrorMessage.Project.PROJECT_NAME_TOO_LONG, entityCreationException.getMessage());
        Assertions.assertEquals(0, project.size());
    }
}