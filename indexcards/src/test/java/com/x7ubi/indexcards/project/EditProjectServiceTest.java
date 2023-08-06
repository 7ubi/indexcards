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
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class EditProjectServiceTest extends ProjectTestConfig {

    @Test
    public void editProjectTest() {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest("edited project");

        // when
        ResultResponse result = this.editProjectService
                .editProject(createProjectRequest, project.getId(), user.getUsername());

        // then
        project = this.projectRepo.findProjectByName(createProjectRequest.getName()).get(0);
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 0);
        assertThat(project.getName()).isEqualTo(createProjectRequest.getName());
    }

    @Test
    public void editProjectWithNonexistentUser() {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject1");

        // when
        ResultResponse result
                = this.editProjectService.editProject(createProjectRequest, project.getId(), "nonexistent");

        // then
        assertEquals(WRONGFULLY_SUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 1);
        assertThat(result.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.Project.USERNAME_NOT_FOUND);
        assertThat(this.projectRepo.findProjectByName(createProjectRequest.getName()).isEmpty()).isTrue();
        assertThat(this.projectRepo.findProjectByName(this.projects.get(0).getName()).isEmpty()).isFalse();
    }

    @Test
    public void editProjectNameExistsTest() {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject1");

        // when
        ResultResponse result = this.createProjectService.createProject("test", createProjectRequest);
        ResultResponse resultDouble = this.editProjectService.editProject(createProjectRequest, project.getId(), "test");

        // then
        project = this.projectRepo.findProjectByName(createProjectRequest.getName()).get(0);
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 0);
        assertThat(project.getName()).isEqualTo(createProjectRequest.getName());
        assertEquals(WRONGFULLY_SUCCESSFUL, resultDouble.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, resultDouble.getErrorMessages().size(), 1);
        assertThat(resultDouble.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.Project.PROJECT_NAME_EXISTS);
        assertThat(this.projectRepo.findProjectByName(createProjectRequest.getName()).isEmpty()).isFalse();
        assertThat(this.projectRepo.findProjectByName(this.projects.get(0).getName()).isEmpty()).isFalse();
    }

    @Test
    public void editProjectWithTooLongName() {
        // given
        String projectName = new String(new char[101]).replace('\0', 'A');
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName(projectName);

        // when
        ResultResponse result = this.createProjectService.createProject("test", createProjectRequest);

        // then
        List<Project> project = this.projectRepo.findProjectByName(projectName);
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 1);
        assertThat(result.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.Project.PROJECT_NAME_TOO_LONG);
        assertThat(project.size()).isEqualTo(0);
        assertThat(this.projectRepo.findProjectByName(this.projects.get(0).getName()).isEmpty()).isFalse();
    }
}
