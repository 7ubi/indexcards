package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.response.indexcard.DueIndexCardResponse;
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
import java.util.HashSet;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=USER"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DueIndexCardServiceTest extends IndexCardTestConfig {

    // Fixed mid-day so "later today" and "tomorrow" are unambiguous.
    private final LocalDateTime now = LocalDateTime.of(2026, 9, 27, 12, 0);

    private IndexCard createIndexCardWithDueDate(Project project, LocalDateTime dueDate) {
        IndexCard card = new IndexCard();
        card.setQuestion(StandardCharsets.UTF_8.encode("Question").array());
        card.setAnswer(StandardCharsets.UTF_8.encode("Answer").array());
        card.setProject(project);
        card.setDueDate(dueDate);
        return this.indexCardRepo.save(card);
    }

    private Project createProject(String name, User owner) {
        Project project = new Project(name, null);
        project.setUser(owner);
        project.setIndexCards(new HashSet<>());
        return this.projectRepo.save(project);
    }

    private List<Long> dueIds() throws EntityNotFoundException {
        return this.dueIndexCardService.getDueIndexCards(user.getUsername(), now).stream()
                .map(DueIndexCardResponse::getIndexCardId)
                .toList();
    }

    @Test
    public void includesOverdueAndLaterTodayButNotTomorrowTest() throws EntityNotFoundException {
        // given
        IndexCard overdue = createIndexCardWithDueDate(this.projects.getFirst(), now.minusDays(3));
        IndexCard laterToday = createIndexCardWithDueDate(this.projects.getFirst(), now.withHour(23).withMinute(59));
        IndexCard tomorrow = createIndexCardWithDueDate(this.projects.getFirst(), now.plusDays(1).withHour(0).withMinute(0));

        // when
        List<Long> ids = dueIds();

        // then
        Assertions.assertEquals(List.of(overdue.getId(), laterToday.getId()), ids);
        Assertions.assertFalse(ids.contains(tomorrow.getId()));
    }

    @Test
    public void treatsNeverScheduledCardAsDueTest() throws EntityNotFoundException {
        // given
        createIndexCard();

        // when
        List<Long> ids = dueIds();

        // then
        Assertions.assertEquals(List.of(this.indexCard.getId()), ids);
    }

    @Test
    public void spansProjectsOrderedByMostOverdueFirstTest() throws EntityNotFoundException {
        // given
        Project secondProject = createProject("SecondProject", this.user);
        IndexCard lessOverdue = createIndexCardWithDueDate(this.projects.getFirst(), now.minusDays(1));
        IndexCard mostOverdue = createIndexCardWithDueDate(secondProject, now.minusDays(5));

        // when
        List<DueIndexCardResponse> due = this.dueIndexCardService.getDueIndexCards(user.getUsername(), now);

        // then
        Assertions.assertEquals(2, due.size());
        Assertions.assertEquals(mostOverdue.getId(), due.getFirst().getIndexCardId());
        Assertions.assertEquals(secondProject.getId(), due.getFirst().getProjectId());
        Assertions.assertEquals("SecondProject", due.getFirst().getProjectName());
        Assertions.assertEquals("Question", due.getFirst().getQuestion());
        Assertions.assertEquals(lessOverdue.getId(), due.get(1).getIndexCardId());
        Assertions.assertEquals(this.projects.getFirst().getId(), due.get(1).getProjectId());
    }

    @Test
    public void excludesOtherUsersCardsTest() throws EntityNotFoundException {
        // given
        Project otherUsersProject = createProject("OtherProject", this.user2);
        IndexCard foreignCard = createIndexCardWithDueDate(otherUsersProject, now.minusDays(1));
        IndexCard ownCard = createIndexCardWithDueDate(this.projects.getFirst(), now.minusDays(1));

        // when
        List<Long> ids = dueIds();

        // then
        Assertions.assertEquals(List.of(ownCard.getId()), ids);
        Assertions.assertFalse(ids.contains(foreignCard.getId()));
    }

    @Test
    public void unknownUserTest() {
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.dueIndexCardService.getDueIndexCards("doesNotExist", now));
    }
}
