package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=update"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CreateProjectServiceTest extends ProjectTestConfig {

    @Test
    public void createProjectTest() {
        // Given
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject");

        // When
        ResultResponse result = this.createProjectService.createProject("test", createProjectRequest);

        // Then
        Project project = this.projectRepo.findProjectByName("TestProject").get(0);
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 0);
        assertThat(project.getName()).isEqualTo("TestProject");
    }
    @Test
    public void createProjectWithNonexistentUser() {
        // Given
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject");

        // When
        ResultResponse result = this.createProjectService.createProject("nonexistent", createProjectRequest);

        // Then
        assertEquals(WRONGFULLY_SUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 1);
        assertThat(result.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.Project.USERNAME_NOT_FOUND);
    }

    @Test
    public void createProjectTwiceTest() {
        // Given
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject");

        // When
        ResultResponse result = this.createProjectService.createProject("test", createProjectRequest);
        ResultResponse resultDouble = this.createProjectService.createProject("test", createProjectRequest);

        // Then
        Project project = this.projectRepo.findProjectByName("TestProject").get(0);
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 0);
        assertThat(project.getName()).isEqualTo("TestProject");
        assertEquals(WRONGFULLY_SUCCESSFUL, resultDouble.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, resultDouble.getErrorMessages().size(), 1);
        assertThat(resultDouble.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.Project.PROJECT_NAME_EXISTS);
    }

    @Test
    public void createProjectTwiceFromDifferentUserTest() {
        // Given
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject");

        // When
        ResultResponse result = this.createProjectService.createProject("test", createProjectRequest);
        ResultResponse resultDouble = this.createProjectService.createProject("test2", createProjectRequest);

        // Then
        List<Project> project = this.projectRepo.findProjectByName("TestProject");
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 0);
        assertThat(project.get(0).getName()).isEqualTo("TestProject");
        assertEquals(WRONGFULLY_SUCCESSFUL, resultDouble.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, resultDouble.getErrorMessages().size(), 0);
        assertThat(project.get(1).getName()).isEqualTo("TestProject");
    }

    @Test
    public void createProjectWithTooLongName() {
        // Given
        String projectName = new String(new char[101]).replace('\0', 'A');
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName(projectName);

        // When
        ResultResponse result = this.createProjectService.createProject("test", createProjectRequest);

        // Then
        List<Project> project = this.projectRepo.findProjectByName(projectName);
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 1);
        assertThat(result.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.Project.PROJECT_NAME_TOO_LONG);
        assertThat(project.size()).isEqualTo(0);
    }
}
