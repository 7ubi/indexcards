package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.response.project.UserProjectsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;

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

    @BeforeEach
    public void createProjects() {
        this.projects = new ArrayList<>();
        this.projects.add(new Project("TestProject", null));
        this.projectRepo.save(this.projects.get(0));

        User userToEdit = this.userRepo.findByUsername(this.user.getUsername()).get();
        userToEdit.setProjects(projects);
        this.userRepo.save(userToEdit);
    }

    @Test
    public void getAllUserProjectsTest() {
        // when
        UserProjectsResponse userProjectsResponse = this.projectService.getUserProjects(user.getUsername());

        // then
        assertEquals(WRONGFULLY_UNSUCCESSFUL, userProjectsResponse.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, userProjectsResponse.getErrorMessages().isEmpty(), true);
        assertThat(userProjectsResponse.getProjectResponses().size()).isEqualTo(1);
        assertThat(userProjectsResponse.getProjectResponses().get(0).getName()).isEqualTo(this.projects.get(0).getName());
    }

    @Test
    public void getAllUserProjectsWithWrongUsernameTest() {
        // when
        UserProjectsResponse userProjectsResponse = this.projectService.getUserProjects("wrong username");

        // then
        assertEquals(WRONGFULLY_UNSUCCESSFUL, userProjectsResponse.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, userProjectsResponse.getErrorMessages().size(), 1);
        assertThat(userProjectsResponse.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.Project.USERNAME_NOT_FOUND);
        assertThat(userProjectsResponse.getProjectResponses()).isEqualTo(null);
    }
}
