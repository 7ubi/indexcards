package com.x7ubi.indexcards.project;

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
}
