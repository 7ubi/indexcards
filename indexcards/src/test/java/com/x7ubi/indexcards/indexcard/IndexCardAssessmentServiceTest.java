package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;

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
public class IndexCardAssessmentServiceTest extends IndexCardTestConfig {

    private IndexCard indexCard;

    @BeforeEach
    public void setupIndexCards() {
        this.indexCard = new IndexCard();
        this.indexCard.setQuestion("Question");
        this.indexCard.setAnswer("Answer");

        this.indexCardRepo.save(this.indexCard);
        Set<IndexCard> indexCards = new HashSet<>();
        this.indexCard = this.indexCardRepo.findIndexCardByQuestion(this.indexCard.getQuestion());
        indexCards.add(this.indexCard);
        this.projects.get(0).setIndexCards(indexCards);
        this.projectRepo.save(this.projects.get(0));
    }

    @Test
    public void assessIndexCardTest() {
        // given
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId());

        // when
        ResultResponse result = this.indexCardAssessmentService.assessIndexCard(assessmentRequest);

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().isEmpty(), true);
        assertThat(newIndexCard.getQuestion()).isEqualTo(newIndexCard.getQuestion());
        assertThat(newIndexCard.getAnswer()).isEqualTo(newIndexCard.getAnswer());
        assertThat(newIndexCard.getAssessment()).isEqualTo(Assessment.GOOD);
    }

    @Test
    public void assessIndexCardWithNonexistentIndexCardTest() {
        // given
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId() + 1);

        // when
        ResultResponse result = this.indexCardAssessmentService.assessIndexCard(assessmentRequest);

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, result.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, result.getErrorMessages().size(), 1);
        assertThat(result.getErrorMessages().get(0).getMessage()).isEqualTo(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND);
        assertThat(newIndexCard.getQuestion()).isEqualTo(newIndexCard.getQuestion());
        assertThat(newIndexCard.getAnswer()).isEqualTo(newIndexCard.getAnswer());
        assertThat(newIndexCard.getAssessment()).isEqualTo(Assessment.UNRATED);
    }
}
