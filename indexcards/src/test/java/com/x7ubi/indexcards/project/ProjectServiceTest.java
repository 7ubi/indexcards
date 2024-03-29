package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.response.project.UserProjectResponse;
import com.x7ubi.indexcards.response.project.UserProjectsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
}
