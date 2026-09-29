package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardsRequest;
import com.x7ubi.indexcards.request.indexcard.IndexCardContentRequest;
import com.x7ubi.indexcards.service.indexcard.CreateIndexCardService;
import com.x7ubi.indexcards.request.indexcard.IndexCardCsvImportRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=USER"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CreateIndexCardServiceTest extends IndexCardTestConfig {

    @Test
    public void createIndexCardTest() throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                projects.getFirst().getId(),
                "Question",
                "Answer"
        );

        // when
        this.createIndexCardService.createIndexCard(user.getUsername(), createIndexCardRequest);

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode(createIndexCardRequest.getQuestion()).array());
        Assertions.assertEquals(createIndexCardRequest.getQuestion(), String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getQuestion()))));
        Assertions.assertEquals(createIndexCardRequest.getAnswer(), String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getAnswer()))));
        Assertions.assertEquals(Assessment.UNRATED, indexCard.getAssessment());
    }

    @Test
    public void importIndexCardsFromCsvWithEscapedQuotesTest() throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        // given
        IndexCardCsvImportRequest indexCardCsvImportRequest = new IndexCardCsvImportRequest();
        indexCardCsvImportRequest.setProjectId(projects.getFirst().getId());
        indexCardCsvImportRequest.setCsv("\"He said \"\"hi\"\"\",\"Answer\"");

        // when
        this.createIndexCardService.importIndexCardsFromCsv(user.getUsername(), indexCardCsvImportRequest);

        // then
        String expectedQuestion = "He said \"hi\"";
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode(expectedQuestion).array());
        Assertions.assertNotNull(indexCard);
        Assertions.assertEquals(expectedQuestion, String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getQuestion()))).trim());
        Assertions.assertEquals("Answer", String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getAnswer()))).trim());
    }

    @Test
    public void createIndexCardWithNonexistentProjectTest() {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                projects.getFirst().getId() + 1,
                "Question",
                "Answer"
        );

        // when
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.createIndexCardService.createIndexCard(user.getUsername(), createIndexCardRequest));

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode(createIndexCardRequest.getQuestion()).array());
        Assertions.assertEquals(ErrorMessage.IndexCards.PROJECT_NOT_FOUND, entityNotFoundException.getMessage());
        Assertions.assertNull(indexCard);
    }

    @Test
    public void createIndexCardWithUnauthorizedUserTest() {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                projects.getFirst().getId(),
                "Question",
                "Answer"
        );

        // when
        UnauthorizedException unauthorizedException = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.createIndexCardService.createIndexCard(user2.getUsername(), createIndexCardRequest));

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode(createIndexCardRequest.getQuestion()).array());
        Assertions.assertEquals(ErrorMessage.Project.USER_NOT_PROJECT_OWNER, unauthorizedException.getMessage());
        Assertions.assertNull(indexCard);
    }

    @Test
    public void importIndexCardsFromCsvWithUnauthorizedUserTest() {
        // given
        IndexCardCsvImportRequest indexCardCsvImportRequest = new IndexCardCsvImportRequest();
        indexCardCsvImportRequest.setProjectId(projects.getFirst().getId());
        indexCardCsvImportRequest.setCsv("Injected question,Injected answer");

        // when
        UnauthorizedException unauthorizedException = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.createIndexCardService.importIndexCardsFromCsv(user2.getUsername(), indexCardCsvImportRequest));

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode("Injected question").array());
        Assertions.assertEquals(ErrorMessage.Project.USER_NOT_PROJECT_OWNER, unauthorizedException.getMessage());
        Assertions.assertNull(indexCard);
    }

    @Test
    public void importIndexCardsFromCsvWithNonexistentProjectTest() {
        // given
        IndexCardCsvImportRequest indexCardCsvImportRequest = new IndexCardCsvImportRequest();
        indexCardCsvImportRequest.setProjectId(projects.getFirst().getId() + 1);
        indexCardCsvImportRequest.setCsv("Question,Answer");

        // when
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.createIndexCardService.importIndexCardsFromCsv(user.getUsername(), indexCardCsvImportRequest));

        // then
        Assertions.assertEquals(ErrorMessage.IndexCards.PROJECT_NOT_FOUND, entityNotFoundException.getMessage());
    }

    @Test
    public void createIndexCardInArchivedProjectTest() {
        // given
        this.archiveProject();
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                projects.getFirst().getId(),
                "Question",
                "Answer"
        );

        // when
        EntityCreationException entityCreationException = Assertions.assertThrows(EntityCreationException.class, () ->
                this.createIndexCardService.createIndexCard(user.getUsername(), createIndexCardRequest));

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode(createIndexCardRequest.getQuestion()).array());
        Assertions.assertEquals(ErrorMessage.IndexCards.PROJECT_ARCHIVED, entityCreationException.getMessage());
        Assertions.assertNull(indexCard);
    }

    @Test
    public void importIndexCardsFromCsvInArchivedProjectTest() {
        // given
        this.archiveProject();
        IndexCardCsvImportRequest indexCardCsvImportRequest = new IndexCardCsvImportRequest();
        indexCardCsvImportRequest.setProjectId(projects.getFirst().getId());
        indexCardCsvImportRequest.setCsv("Archived question,Archived answer");

        // when
        EntityCreationException entityCreationException = Assertions.assertThrows(EntityCreationException.class, () ->
                this.createIndexCardService.importIndexCardsFromCsv(user.getUsername(), indexCardCsvImportRequest));

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode("Archived question").array());
        Assertions.assertEquals(ErrorMessage.IndexCards.PROJECT_ARCHIVED, entityCreationException.getMessage());
        Assertions.assertNull(indexCard);
    }

    @Test
    public void createIndexCardsTest() throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        // given
        CreateIndexCardsRequest request = new CreateIndexCardsRequest(projects.getFirst().getId(), List.of(
                new IndexCardContentRequest("  What is $E = mc^2$?  ", "Mass-energy equivalence"),
                new IndexCardContentRequest("Bulk question 2", "**Bulk** answer 2")));

        // when
        this.createIndexCardService.createIndexCards(user.getUsername(), request);

        // then
        IndexCard first = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode("What is $E = mc^2$?").array());
        IndexCard second = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode("Bulk question 2").array());
        Assertions.assertNotNull(first);
        Assertions.assertNotNull(second);
        // Same byte encoding as every other create path (IndexCardMapper), including its trailing padding.
        Assertions.assertArrayEquals(StandardCharsets.UTF_8.encode("**Bulk** answer 2").array(), second.getAnswer());
        Assertions.assertEquals(Assessment.UNRATED, first.getAssessment());
        Assertions.assertEquals(projects.getFirst().getId(), first.getProject().getId());
    }

    @Test
    public void createIndexCardsWithEmptyCardSavesNothingTest() {
        // given
        CreateIndexCardsRequest request = new CreateIndexCardsRequest(projects.getFirst().getId(), List.of(
                new IndexCardContentRequest("Valid bulk question", "Valid answer"),
                new IndexCardContentRequest("   ", "Answer without question")));

        // when
        EntityCreationException exception = Assertions.assertThrows(EntityCreationException.class, () ->
                this.createIndexCardService.createIndexCards(user.getUsername(), request));

        // then
        Assertions.assertEquals(ErrorMessage.IndexCards.INDEXCARD_EMPTY, exception.getMessage());
        Assertions.assertNull(this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode("Valid bulk question").array()));
    }

    @Test
    public void createIndexCardsWithoutCardsTest() {
        CreateIndexCardsRequest request = new CreateIndexCardsRequest(projects.getFirst().getId(), List.of());

        EntityCreationException exception = Assertions.assertThrows(EntityCreationException.class, () ->
                this.createIndexCardService.createIndexCards(user.getUsername(), request));

        Assertions.assertEquals(ErrorMessage.IndexCards.INDEXCARD_EMPTY, exception.getMessage());
    }

    @Test
    public void createIndexCardsWithTooManyCardsTest() {
        List<IndexCardContentRequest> cards = new ArrayList<>();
        for (int i = 0; i <= CreateIndexCardService.MAX_CARDS_PER_REQUEST; i++) {
            cards.add(new IndexCardContentRequest("Q" + i, "A" + i));
        }
        CreateIndexCardsRequest request = new CreateIndexCardsRequest(projects.getFirst().getId(), cards);

        EntityCreationException exception = Assertions.assertThrows(EntityCreationException.class, () ->
                this.createIndexCardService.createIndexCards(user.getUsername(), request));

        Assertions.assertEquals(ErrorMessage.IndexCards.TOO_MANY_INDEXCARDS, exception.getMessage());
    }

    @Test
    public void createIndexCardsWithUnauthorizedUserTest() {
        CreateIndexCardsRequest request = new CreateIndexCardsRequest(projects.getFirst().getId(),
                List.of(new IndexCardContentRequest("Foreign bulk question", "Answer")));

        UnauthorizedException exception = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.createIndexCardService.createIndexCards(user2.getUsername(), request));

        Assertions.assertEquals(ErrorMessage.Project.USER_NOT_PROJECT_OWNER, exception.getMessage());
        Assertions.assertNull(this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode("Foreign bulk question").array()));
    }

    @Test
    public void createIndexCardsInArchivedProjectTest() {
        this.archiveProject();
        CreateIndexCardsRequest request = new CreateIndexCardsRequest(projects.getFirst().getId(),
                List.of(new IndexCardContentRequest("Archived bulk question", "Answer")));

        EntityCreationException exception = Assertions.assertThrows(EntityCreationException.class, () ->
                this.createIndexCardService.createIndexCards(user.getUsername(), request));

        Assertions.assertEquals(ErrorMessage.IndexCards.PROJECT_ARCHIVED, exception.getMessage());
    }
}
