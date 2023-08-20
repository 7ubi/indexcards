package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.request.indexcard.DeleteIndexCardRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.junit.jupiter.api.BeforeEach;
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
public class DeleteIndexCardServiceTest extends IndexCardTestConfig {


    @BeforeEach
    public void setupIndexCards() {
        this.createIndexCard();
    }

    @Test
    public void deleteIndexCardTest() {
        // given
        DeleteIndexCardRequest deleteIndexCardRequest = new DeleteIndexCardRequest(
                this.indexCard.getIndexcardId(),
                this.projects.get(0).getId()
        );

        // when
        ResultResponse result = this.deleteIndexCardService.deleteIndexCard(deleteIndexCardRequest);

        // then
        Project project = projectRepo.findProjectByProjectId(this.projects.get(0).getId());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().isEmpty(), true);
        assertThat(project.getIndexCards().size()).isEqualTo(0);
    }

    @Test
    public void deleteIndexCardWrongProjectTest() {
        // given
        DeleteIndexCardRequest deleteIndexCardRequest = new DeleteIndexCardRequest(
                this.indexCard.getIndexcardId(),
                this.projects.get(0).getId() + 1
        );

        // when
        ResultResponse result = this.deleteIndexCardService.deleteIndexCard(deleteIndexCardRequest);

        // then
        Project project = projectRepo.findProjectByProjectId(this.projects.get(0).getId());
        assertEquals(WRONGFULLY_SUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().isEmpty(), false);
        assertThat(result.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.IndexCards.PROJECT_NOT_FOUND);
        assertThat(project.getIndexCards().size()).isEqualTo(1);
    }

    @Test
    public void deleteIndexCardWrongIndexCardTest() {
        // given
        DeleteIndexCardRequest deleteIndexCardRequest = new DeleteIndexCardRequest(
                this.indexCard.getIndexcardId() + 1,
                this.projects.get(0).getId()
        );

        // when
        ResultResponse result = this.deleteIndexCardService.deleteIndexCard(deleteIndexCardRequest);

        // then
        Project project = projectRepo.findProjectByProjectId(this.projects.get(0).getId());
        assertEquals(WRONGFULLY_SUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().isEmpty(), false);
        assertThat(result.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND);
        assertThat(project.getIndexCards().size()).isEqualTo(1);
    }
}
