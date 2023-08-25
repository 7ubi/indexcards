package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.request.indexcard.EditIndexCardRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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
public class EditIndexCardServiceTest extends IndexCardTestConfig {

    @BeforeEach
    public void setupIndexCards() {
        this.createIndexCard();
    }

    @Test
    public void editIndexCardTest() {
        // given
        EditIndexCardRequest editIndexCardRequest = new EditIndexCardRequest(this.indexCard.getId(),
                "Edit Question", "Edit Answer");

        // when
        ResultResponse result = this.editIndexCardService.editIndexCard(editIndexCardRequest);

        // then
        IndexCard editedIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().isEmpty(), true);
        assertThat(String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(editedIndexCard.getQuestion()))))
                .contains(editIndexCardRequest.getQuestion());
        assertThat(String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(editedIndexCard.getAnswer()))))
                .contains(editIndexCardRequest.getAnswer());
        assertThat(editedIndexCard.getAssessment()).isEqualTo(Assessment.UNRATED);
    }

    @Test
    public void editIndexCardWithWrongIdTest() {
        // given
        EditIndexCardRequest editIndexCardRequest = new EditIndexCardRequest(this.indexCard.getId() + 1,
                "Edit Question", "Edit Answer");

        // when
        ResultResponse result = this.editIndexCardService.editIndexCard(editIndexCardRequest);

        // then
        IndexCard editedIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 1);
        assertThat(result.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND);
        assertThat(String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(editedIndexCard.getQuestion()))))
                .isEqualTo("Question");
        assertThat(String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(editedIndexCard.getAnswer()))))
                .isEqualTo("Answer");
        assertThat(editedIndexCard.getAssessment()).isEqualTo(Assessment.UNRATED);
    }
}
