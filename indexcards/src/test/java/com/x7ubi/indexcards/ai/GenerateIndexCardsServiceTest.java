package com.x7ubi.indexcards.ai;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.AiGenerationException;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.response.ai.GeneratedCardsResponse;
import com.x7ubi.indexcards.service.ai.CardGenerationInput;
import com.x7ubi.indexcards.service.ai.GeneratedCard;
import com.x7ubi.indexcards.service.indexcard.GenerateIndexCardsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class GenerateIndexCardsServiceTest extends AiTestConfig {

    @Autowired
    private GenerateIndexCardsService generateIndexCardsService;

    private void clientReturns(GeneratedCard... cards) throws AiGenerationException {
        when(this.cardGenerationClient.generate(any(), any())).thenReturn(List.of(cards));
    }

    private Long projectId() {
        return this.projects.getFirst().getId();
    }

    private CardGenerationInput capturedInput() throws AiGenerationException {
        ArgumentCaptor<CardGenerationInput> input = ArgumentCaptor.forClass(CardGenerationInput.class);
        verify(this.cardGenerationClient).generate(eq(API_KEY), input.capture());
        return input.getValue();
    }

    @Test
    public void generateUsesTheUsersKeyAndSavesNothingTest() throws Exception {
        saveApiKey();
        clientReturns(new GeneratedCard("What is ATP?", "The cell's energy carrier"),
                new GeneratedCard("Where is ATP produced?", "In the **mitochondria**"));

        GeneratedCardsResponse response = this.generateIndexCardsService.generateIndexCards(
                this.user.getUsername(), projectId(), "  Mitochondria produce ATP.  ", 5, null);

        Assertions.assertEquals(2, response.getCards().size());
        Assertions.assertEquals("What is ATP?", response.getCards().getFirst().getQuestion());
        Assertions.assertNull(this.indexCardRepo.findIndexCardByQuestion(
                StandardCharsets.UTF_8.encode("What is ATP?").array()));
        CardGenerationInput input = capturedInput();
        Assertions.assertEquals("Mitochondria produce ATP.", input.notes());
        Assertions.assertEquals(5, input.cardCount());
        Assertions.assertNull(input.pdf());
    }

    @Test
    public void withoutApiKeyTest() {
        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class, () ->
                this.generateIndexCardsService.generateIndexCards(this.user.getUsername(), projectId(), "notes", 5, null));

        Assertions.assertEquals(ErrorMessage.Ai.API_KEY_MISSING, exception.getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verifyNoInteractions(this.cardGenerationClient);
    }

    @Test
    public void unreadableApiKeyTest() {
        this.user.setAiApiKeyEncrypted("v1:AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        this.userRepo.save(this.user);

        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class, () ->
                this.generateIndexCardsService.generateIndexCards(this.user.getUsername(), projectId(), "notes", 5, null));

        Assertions.assertEquals(ErrorMessage.Ai.API_KEY_UNREADABLE, exception.getMessage());
        verifyNoInteractions(this.cardGenerationClient);
    }

    @Test
    public void suggestionsAreSanitizedAndLimitedTest() throws Exception {
        saveApiKey();
        clientReturns(new GeneratedCard("  Q1  ", "  A1 "),
                new GeneratedCard(" ", "answer without question"),
                new GeneratedCard("q1", "duplicate question, different case"),
                new GeneratedCard("Q2", "A2"),
                new GeneratedCard("Q3", "A3"));

        GeneratedCardsResponse response = this.generateIndexCardsService.generateIndexCards(
                this.user.getUsername(), projectId(), "notes", 2, null);

        Assertions.assertEquals(2, response.getCards().size());
        Assertions.assertEquals("Q1", response.getCards().get(0).getQuestion());
        Assertions.assertEquals("A1", response.getCards().get(0).getAnswer());
        Assertions.assertEquals("Q2", response.getCards().get(1).getQuestion());
    }

    @Test
    public void cardCountIsClampedTest() throws Exception {
        saveApiKey();
        clientReturns(new GeneratedCard("Q", "A"));

        this.generateIndexCardsService.generateIndexCards(this.user.getUsername(), projectId(), "notes", 500, null);

        Assertions.assertEquals(30, capturedInput().cardCount());
    }

    @Test
    public void notOwnerTest() {
        Assertions.assertThrows(UnauthorizedException.class, () -> this.generateIndexCardsService.generateIndexCards(
                this.user2.getUsername(), projectId(), "notes", 5, null));
        verifyNoInteractions(this.cardGenerationClient);
    }

    @Test
    public void archivedProjectTest() {
        saveApiKey();
        archiveProject();

        EntityCreationException exception = Assertions.assertThrows(EntityCreationException.class, () ->
                this.generateIndexCardsService.generateIndexCards(this.user.getUsername(), projectId(), "notes", 5, null));

        Assertions.assertEquals(ErrorMessage.IndexCards.PROJECT_ARCHIVED, exception.getMessage());
        verifyNoInteractions(this.cardGenerationClient);
    }

    @Test
    public void emptyInputTest() {
        saveApiKey();

        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class, () ->
                this.generateIndexCardsService.generateIndexCards(this.user.getUsername(), projectId(), "   ", 5, null));

        Assertions.assertEquals(ErrorMessage.Ai.INPUT_EMPTY, exception.getMessage());
        verifyNoInteractions(this.cardGenerationClient);
    }

    @Test
    public void notesTooLongTest() {
        saveApiKey();

        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class, () ->
                this.generateIndexCardsService.generateIndexCards(
                        this.user.getUsername(), projectId(), "x".repeat(101), 5, null));

        Assertions.assertEquals(ErrorMessage.Ai.NOTES_TOO_LONG, exception.getMessage());
        verifyNoInteractions(this.cardGenerationClient);
    }

    @Test
    public void failureReleasesTheLockTest() throws Exception {
        saveApiKey();
        when(this.cardGenerationClient.generate(any(), any()))
                .thenThrow(new AiGenerationException(ErrorMessage.Ai.TIMEOUT, HttpStatus.GATEWAY_TIMEOUT))
                .thenReturn(List.of(new GeneratedCard("Q", "A")));

        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class, () ->
                this.generateIndexCardsService.generateIndexCards(this.user.getUsername(), projectId(), "notes", 5, null));
        Assertions.assertEquals(ErrorMessage.Ai.TIMEOUT, exception.getMessage());

        Assertions.assertEquals(1, this.generateIndexCardsService.generateIndexCards(
                this.user.getUsername(), projectId(), "notes", 5, null).getCards().size());
    }

    @Test
    public void noUsableCardsTest() throws Exception {
        saveApiKey();
        clientReturns(new GeneratedCard(" ", " "));

        AiGenerationException exception = Assertions.assertThrows(AiGenerationException.class, () ->
                this.generateIndexCardsService.generateIndexCards(this.user.getUsername(), projectId(), "notes", 5, null));

        Assertions.assertEquals(ErrorMessage.Ai.NO_CARDS, exception.getMessage());
    }
}
