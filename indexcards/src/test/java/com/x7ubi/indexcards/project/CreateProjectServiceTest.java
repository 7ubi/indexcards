package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
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
public class CreateProjectServiceTest extends ProjectTestConfig {

    private static final String WRONGFULLY_UNSUCCESSFUL = "Success was expected!";

    @Test
    public void createProjectTest() {
        // Given
        CreateProjectRequest createProjectRequest = new CreateProjectRequest();
        createProjectRequest.setName("TestProject");

        // When
        ResultResponse result = this.createProjectService.createProject("test", createProjectRequest);

        // Then
        Project project = this.projectRepo.findProjectByName("TestProject");
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertThat(project.getName()).isEqualTo("TestProject");
    }
}
