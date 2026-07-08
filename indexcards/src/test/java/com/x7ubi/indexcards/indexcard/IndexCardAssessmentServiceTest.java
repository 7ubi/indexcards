package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
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

import java.time.LocalDate;
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
    public void assessIndexCardTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId());

        // when
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);

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
    public void assessIndexCardWithGoodSetsSchedulingTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId());

        // when
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        Assertions.assertEquals(1, newIndexCard.getRepetitions());
        Assertions.assertEquals(1, newIndexCard.getIntervalDays());
        Assertions.assertEquals(2.6, newIndexCard.getEaseFactor(), 0.0001);
        Assertions.assertEquals(
                LocalDateTime.now().plusDays(1).getDayOfYear(), newIndexCard.getDueDate().getDayOfYear());
    }

    @Test
    public void assessIndexCardGoodStreakGrowsIntervalTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId());

        // when
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        Assertions.assertEquals(3, newIndexCard.getRepetitions());
        Assertions.assertEquals(17, newIndexCard.getIntervalDays());
        Assertions.assertEquals(2.8, newIndexCard.getEaseFactor(), 0.0001);
    }

    @Test
    public void assessIndexCardOkStreakGrowsIntervalSlowlyTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.OK);
        assessmentRequest.setIndexCardId(this.indexCard.getId());

        // when
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        Assertions.assertEquals(3, newIndexCard.getRepetitions());
        Assertions.assertEquals(6, newIndexCard.getIntervalDays());
        Assertions.assertEquals(2.35, newIndexCard.getEaseFactor(), 0.0001);
    }

    @Test
    public void assessIndexCardBadResetsSchedulingTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        AssessmentRequest goodRequest = new AssessmentRequest();
        goodRequest.setAssessment(Assessment.GOOD);
        goodRequest.setIndexCardId(this.indexCard.getId());

        AssessmentRequest badRequest = new AssessmentRequest();
        badRequest.setAssessment(Assessment.BAD);
        badRequest.setIndexCardId(this.indexCard.getId());

        // when
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), goodRequest);
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), badRequest);

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        Assertions.assertEquals(0, newIndexCard.getRepetitions());
        Assertions.assertEquals(1, newIndexCard.getIntervalDays());
        Assertions.assertEquals(2.4, newIndexCard.getEaseFactor(), 0.0001);
    }

    @Test
    public void assessIndexCardCompressesIntervalWhenExamDateApproachingTest()
            throws EntityNotFoundException, UnauthorizedException {
        // given
        this.projects.get(0).setExamDate(LocalDate.now().plusDays(4));
        this.projectRepo.save(this.projects.get(0));

        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId());

        // when
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        Assertions.assertEquals(2, newIndexCard.getRepetitions());
        // uncompressed interval would be 6 (second GOOD); daysUntilExam=4 caps it at 4/2=2
        Assertions.assertEquals(2, newIndexCard.getIntervalDays());
        Assertions.assertEquals(2.7, newIndexCard.getEaseFactor(), 0.0001);
        Assertions.assertEquals(
                LocalDateTime.now().plusDays(2).getDayOfYear(), newIndexCard.getDueDate().getDayOfYear());
    }

    @Test
    public void assessIndexCardIgnoresPastExamDateTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        this.projects.get(0).setExamDate(LocalDate.now().minusDays(1));
        this.projectRepo.save(this.projects.get(0));

        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId());

        // when
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);
        this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest);

        // then
        IndexCard newIndexCard = this.indexCardRepo.findIndexCardByIndexcardId(this.indexCard.getId());
        Assertions.assertEquals(2, newIndexCard.getRepetitions());
        Assertions.assertEquals(6, newIndexCard.getIntervalDays());
    }

    @Test
    public void assessIndexCardWithNonexistentIndexCardTest() {
        // given
        AssessmentRequest assessmentRequest = new AssessmentRequest();
        assessmentRequest.setAssessment(Assessment.GOOD);
        assessmentRequest.setIndexCardId(this.indexCard.getId() + 1);

        // when
        EntityNotFoundException entityNotFoundException = Assert.assertThrows(EntityNotFoundException.class, () ->
                this.indexCardAssessmentService.assessIndexCard(user.getUsername(), assessmentRequest));

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
