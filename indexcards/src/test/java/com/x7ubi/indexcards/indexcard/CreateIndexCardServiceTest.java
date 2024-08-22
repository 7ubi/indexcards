package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.Assessment;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import org.junit.Assert;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CreateIndexCardServiceTest extends IndexCardTestConfig {

    @Test
    public void createIndexCardTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                projects.get(0).getId(),
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
    public void createIndexCardWithNonexistentProjectTest() {
        // given
        CreateIndexCardRequest createIndexCardRequest = new CreateIndexCardRequest(
                projects.get(0).getId() + 1,
                "Question",
                "Answer"
        );

        // when
        EntityNotFoundException entityNotFoundException = Assert.assertThrows(EntityNotFoundException.class, () ->
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
                projects.get(0).getId(),
                "Question",
                "Answer"
        );

        // when
        UnauthorizedException unauthorizedException = Assert.assertThrows(UnauthorizedException.class, () ->
                this.createIndexCardService.createIndexCard(user2.getUsername(), createIndexCardRequest));

        // then
        IndexCard indexCard = this.indexCardRepo.findIndexCardByQuestion(StandardCharsets.UTF_8.encode(createIndexCardRequest.getQuestion()).array());
        Assertions.assertEquals(ErrorMessage.Project.USER_NOT_PROJECT_OWNER, unauthorizedException.getMessage());
        Assertions.assertNull(indexCard);
    }
}
