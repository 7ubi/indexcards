package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.IndexCardAssessment;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class IndexCardAssessmentServiceTest extends IndexCardTestConfig {


    @BeforeEach
    public void setupIndexCards() {
        createIndexCard();
    }

    @Test
    public void assessIndexCardTest() throws EntityNotFoundException {
        // given
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId());

        // when
        this.indexCardAssessmentService.assessIndexCard(assessmentRequest);

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        List<IndexCardAssessment> history = new ArrayList<>(newIndexCard.getAssessmentHistory());
        Assertions.assertEquals(newIndexCard.getQuestion(), newIndexCard.getQuestion());
        Assertions.assertEquals(newIndexCard.getAnswer(), newIndexCard.getAnswer());
        Assertions.assertEquals(newIndexCard.getAssessment(), Assessment.GOOD);
        Assertions.assertEquals(history.size(), 1);
        Assertions.assertEquals(history.get(0).getAssessment(), Assessment.GOOD);
        Assertions.assertEquals(history.get(0).getDate().getDayOfYear(), LocalDateTime.now().getDayOfYear());
    }

    @Test
    public void assessIndexCardWithNonexistentIndexCardTest() {
        // given
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId() + 1);

        // when
        EntityNotFoundException entityNotFoundException = Assert.assertThrows(EntityNotFoundException.class, () ->
                this.indexCardAssessmentService.assessIndexCard(assessmentRequest));

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        List<IndexCardAssessment> history = new ArrayList<>(newIndexCard.getAssessmentHistory());
        Assertions.assertEquals(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND, entityNotFoundException.getMessage());
        Assertions.assertEquals(newIndexCard.getQuestion(), newIndexCard.getQuestion());
        Assertions.assertEquals(newIndexCard.getAnswer(), newIndexCard.getAnswer());
        Assertions.assertEquals(newIndexCard.getAssessment(), Assessment.UNRATED);
        Assertions.assertTrue(history.isEmpty());
    }
}
