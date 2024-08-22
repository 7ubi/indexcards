package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import org.junit.jupiter.api.Assertions;
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
    public void editIndexCardTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                null,
                "Question edit",
                "Answer edit"
        );

        // when
        this.editIndexCardService.editIndexCard(user.getUsername(), this.indexCard.getId(), createIndexCardRequest);

        // then
        IndexCard indexCardEdit = this.indexCardRepo.findById(this.indexCard.getId()).orElse(null);
        assert indexCardEdit != null;
        Assertions.assertEquals(indexCard.getId(), indexCardEdit.getId());
        Assertions.assertEquals(createIndexCardRequest.getQuestion(), StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCardEdit.getQuestion())).toString().substring(0, createIndexCardRequest.getQuestion().length()));
        Assertions.assertEquals(createIndexCardRequest.getAnswer(), StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCardEdit.getAnswer())).toString().substring(0, createIndexCardRequest.getAnswer().length()));
    }

    @Test
    public void editIndexCardWithNonexistentIndexCardTest() {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                null,
                "Question edit",
                "Answer edit"
        );

        // when
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.editIndexCardService.editIndexCard(user.getUsername(), this.indexCard.getId() + 1, createIndexCardRequest));

        // then
        Assertions.assertEquals(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND, entityNotFoundException.getMessage());
    }

    @Test
    public void editIndexCardWithUnauthorizedUserTest() {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                null,
                "Question edit",
                "Answer edit"
        );

        // when
        UnauthorizedException unauthorizedException = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.editIndexCardService.editIndexCard(user2.getUsername(), this.indexCard.getId(), createIndexCardRequest));

        // then
        Assertions.assertEquals(ErrorMessage.Project.USER_NOT_PROJECT_OWNER, unauthorizedException.getMessage());
    }
}
