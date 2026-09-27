package com.x7ubi.indexcards.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import com.x7ubi.indexcards.service.indexcard.IndexCardService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=USER"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class IndexCardServiceTest extends IndexCardTestConfig {

    @Autowired
    private IndexCardService indexCardService;

    @BeforeEach
    public void setupIndexCards() {
        createIndexCard();
    }

    @Test
    public void getIndexCardTest() throws EntityNotFoundException, UnauthorizedException {
        // when
        IndexCardResponse response = this.indexCardService.getIndexCard(user.getUsername(), this.indexCard.getId());

        // then
        Assertions.assertEquals(this.indexCard.getId(), response.getIndexCardId());
        Assertions.assertEquals("Question", response.getQuestion());
    }

    @Test
    public void getIndexCardWithUnauthorizedUserTest() {
        // when
        UnauthorizedException unauthorizedException = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.indexCardService.getIndexCard(user2.getUsername(), this.indexCard.getId()));

        // then
        Assertions.assertEquals(ErrorMessage.Project.USER_NOT_PROJECT_OWNER, unauthorizedException.getMessage());
    }

    @Test
    public void getIndexCardWithNonexistentIndexCardTest() {
        // when
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.indexCardService.getIndexCard(user.getUsername(), this.indexCard.getId() + 1));

        // then
        Assertions.assertEquals(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND, entityNotFoundException.getMessage());
    }
}
