package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
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
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CreateIndexCardServiceTest extends IndexCardTestConfig {

    @Test
    public void createIndexCardTest() {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
            projects.get(0).getId(),
            "Question",
            "Answer"
        );

        // when
        ResultResponse result = this.createIndexCardService.createIndexCard(createIndexCardRequest);

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(createIndexCardRequest.getQuestion());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().isEmpty(), true);
        assertThat(indexCard.getQuestion()).isEqualTo(createIndexCardRequest.getQuestion());
        assertThat(indexCard.getAnswer()).isEqualTo(createIndexCardRequest.getAnswer());
        assertThat(indexCard.getAssessment()).isEqualTo(Assessment.UNRATED);
    }

    @Test
    public void createIndexCardWithNonexistentProjectTest() {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                projects.get(0).getId() + 1,
                "Question",
                "Answer"
        );

        // when
        ResultResponse result = this.createIndexCardService.createIndexCard(createIndexCardRequest);

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(createIndexCardRequest.getQuestion());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 1);
        assertThat(result.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.IndexCards.PROJECT_NOT_FOUND);
        assertThat(indexCard).isNull();
    }

    @Test
    public void createIndexCardWithTooLongQuestionAndAnswerTest() {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
            projects.get(0).getId(),
            new String(new char[501]).replace('\0', 'A'),
            new String(new char[501]).replace('\0', 'A')
        );

        // when
        ResultResponse result = this.createIndexCardService.createIndexCard(createIndexCardRequest);

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(createIndexCardRequest.getQuestion());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 2);
        assertThat(result.getErrorMessages().get(0).getMessage())
            .isEqualTo(ErrorMessage.IndexCards.INDEXCARD_QUESTION_TOO_LONG);
        assertThat(result.getErrorMessages().get(1).getMessage())
            .isEqualTo(ErrorMessage.IndexCards.INDEXCARD_ANSWER_TOO_LONG);
        assertThat(indexCard).isNull();
    }
}
