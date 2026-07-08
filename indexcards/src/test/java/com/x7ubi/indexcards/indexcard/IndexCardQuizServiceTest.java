package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class IndexCardQuizServiceTest extends IndexCardTestConfig {

    private IndexCard createIndexCardWithDueDate(LocalDateTime dueDate) {
        IndexCard card = new IndexCard();
        card.setQuestion(StandardCharsets.UTF_8.encode("Question").array());
        card.setAnswer(StandardCharsets.UTF_8.encode("Answer").array());
        card.setProject(this.projects.get(0));
        card.setDueDate(dueDate);
        return this.indexCardRepo.save(card);
    }

    @Test
    public void quizExcludesFutureCardsWhenEnoughDueCardsExistTest()
            throws EntityNotFoundException, UnauthorizedException {
        // given
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 5; i++) {
            createIndexCardWithDueDate(now.minusDays(i + 1));
        }
        IndexCard futureCard = createIndexCardWithDueDate(now.plusDays(10));

        // when
        List<IndexCardResponse> quiz = this.indexCardQuizService.getIndexCardResponsesForQuiz(
                user.getUsername(), this.projects.get(0).getId());

        // then
        Assertions.assertEquals(5, quiz.size());
        List<Long> ids = quiz.stream().map(IndexCardResponse::getIndexCardId).collect(Collectors.toList());
        Assertions.assertFalse(ids.contains(futureCard.getId()));
    }

    @Test
    public void quizOrdersDueCardsByMostOverdueFirstTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        LocalDateTime now = LocalDateTime.now();
        IndexCard mostOverdue = createIndexCardWithDueDate(now.minusDays(5));
        IndexCard lessOverdue = createIndexCardWithDueDate(now.minusDays(1));

        // when
        List<IndexCardResponse> quiz = this.indexCardQuizService.getIndexCardResponsesForQuiz(
                user.getUsername(), this.projects.get(0).getId());

        // then
        Assertions.assertEquals(mostOverdue.getId(), quiz.get(0).getIndexCardId());
        Assertions.assertEquals(lessOverdue.getId(), quiz.get(1).getIndexCardId());
    }

    @Test
    public void quizPadsWithSoonestUpcomingWhenFewCardsAreDueTest()
            throws EntityNotFoundException, UnauthorizedException {
        // given
        LocalDateTime now = LocalDateTime.now();
        IndexCard due1 = createIndexCardWithDueDate(now.minusDays(2));
        IndexCard due2 = createIndexCardWithDueDate(now.minusDays(1));
        IndexCard soonUpcoming = createIndexCardWithDueDate(now.plusDays(1));
        IndexCard laterUpcoming = createIndexCardWithDueDate(now.plusDays(5));

        // when
        List<IndexCardResponse> quiz = this.indexCardQuizService.getIndexCardResponsesForQuiz(
                user.getUsername(), this.projects.get(0).getId());
        List<Long> ids = quiz.stream().map(IndexCardResponse::getIndexCardId).collect(Collectors.toList());

        // then
        Assertions.assertEquals(4, quiz.size());
        Assertions.assertTrue(ids.containsAll(
                List.of(due1.getId(), due2.getId(), soonUpcoming.getId(), laterUpcoming.getId())));
        Assertions.assertTrue(ids.indexOf(soonUpcoming.getId()) < ids.indexOf(laterUpcoming.getId()));
    }

    @Test
    public void quizTreatsLegacyCardWithoutSchedulingAsDueTest()
            throws EntityNotFoundException, UnauthorizedException {
        // given
        createIndexCard();
        IndexCard futureCard = createIndexCardWithDueDate(LocalDateTime.now().plusDays(30));

        // when
        List<IndexCardResponse> quiz = this.indexCardQuizService.getIndexCardResponsesForQuiz(
                user.getUsername(), this.projects.get(0).getId());
        List<Long> ids = quiz.stream().map(IndexCardResponse::getIndexCardId).collect(Collectors.toList());

        // then
        Assertions.assertTrue(ids.indexOf(this.indexCard.getId()) < ids.indexOf(futureCard.getId()));
    }
}
