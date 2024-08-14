package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.response.project.ProjectResponse;
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
public class ProjectServiceTest extends ProjectTestConfig {


    @Test
    public void getAllUserProjectsTest() throws EntityNotFoundException {
        // when
        List<ProjectResponse> responses = this.projectService.getUserProjects(user.getUsername());

        // then
        Assertions.assertEquals(responses.size(), 1);
        Assertions.assertEquals(responses.get(0).getName(), this.projects.get(0).getName());
    }

    @Test
    public void getAllUserProjectsWithWrongUsernameTest() {
        // when
        EntityNotFoundException entityNotFoundException = Assert.assertThrows(EntityNotFoundException.class, () ->
                this.projectService.getUserProjects("wrong username"));

        // then
        Assertions.assertEquals(entityNotFoundException.getMessage(), ErrorMessage.Project.USERNAME_NOT_FOUND);
    }

    @Test
    public void getUserProjectTest() throws EntityNotFoundException {
        // given
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        // when
        ProjectResponse projectResponse = this.projectService.getProject(project.getId());

        // then
        Assertions.assertEquals(this.projects.get(0).getName(), projectResponse.getName());
    }

    @Test
    public void getUserProjectWithWrongProjectIdTest() {
        // given
        long wrongProjectId = this.projects.get(0).getId() + 1;

        // when
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.projectService.getProject(wrongProjectId));

        // then
        Assertions.assertEquals(ErrorMessage.Project.PROJECT_NOT_FOUND, entityNotFoundException.getMessage());
    }
}
