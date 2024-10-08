package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.request.indexcard.DeleteIndexCardRequest;
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

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DeleteIndexCardServiceTest extends IndexCardTestConfig {


    @BeforeEach
    public void setupIndexCards() {
        this.createIndexCard();
    }

    @Test
    public void deleteIndexCardTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        DeleteIndexCardRequest deleteIndexCardRequest = new DeleteIndexCardRequest(
                this.indexCard.getIndexcardId()
        );

        // when
        this.deleteIndexCardService.deleteIndexCard(user.getUsername(), deleteIndexCardRequest);

        // then
        Project project = projectRepo.findProjectByProjectId(this.projects.get(0).getId());
        Assertions.assertEquals(0, project.getIndexCards().size());
    }

    @Test
    public void deleteIndexCardWrongIndexCardTest() {
        // given
        DeleteIndexCardRequest deleteIndexCardRequest = new DeleteIndexCardRequest(
                this.indexCard.getIndexcardId() + 1
        );

        // when
        EntityNotFoundException entityNotFoundException = Assert.assertThrows(EntityNotFoundException.class, () ->
                this.deleteIndexCardService.deleteIndexCard(user.getUsername(), deleteIndexCardRequest));

        // then
        Project project = projectRepo.findProjectByProjectId(this.projects.get(0).getId());
        Assertions.assertEquals(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND, entityNotFoundException.getMessage());
        Assertions.assertEquals(1, project.getIndexCards().size());
    }

    @Test
    public void deleteIndexCardWrongUserTest() {
        // given
        DeleteIndexCardRequest deleteIndexCardRequest = new DeleteIndexCardRequest(
                this.indexCard.getIndexcardId()
        );

        // when
        UnauthorizedException unauthorizedException = Assert.assertThrows(UnauthorizedException.class, () ->
                this.deleteIndexCardService.deleteIndexCard(user2.getUsername(), deleteIndexCardRequest));

        // then
        Project project = projectRepo.findProjectByProjectId(this.projects.get(0).getId());
        Assertions.assertEquals(ErrorMessage.Project.USER_NOT_PROJECT_OWNER, unauthorizedException.getMessage());
        Assertions.assertEquals(1, project.getIndexCards().size());
    }
}
