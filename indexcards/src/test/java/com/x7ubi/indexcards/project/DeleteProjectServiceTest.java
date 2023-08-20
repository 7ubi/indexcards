package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DeleteProjectServiceTest extends ProjectTestConfig {

    private IndexCard indexCard;

    @Autowired
    private IndexCardRepo indexCardRepo;


    private void createIndexCardsForProject() {
        this.indexCard = new IndexCard();
        this.indexCard.setQuestion(StandardCharsets.UTF_8.encode("Question").array());
        this.indexCard.setAnswer(StandardCharsets.UTF_8.encode("Answer").array());

        this.indexCardRepo.save(this.indexCard);
        Set<IndexCard> indexCards = new HashSet<>();
        this.indexCard = this.indexCardRepo.findIndexCardByQuestion(this.indexCard.getQuestion());
        indexCards.add(this.indexCard);
        this.projects.get(0).setIndexCards(indexCards);
        this.projectRepo.save(this.projects.get(0));
    }

    @Test
    public void deleteProjectTest() {
        // given
        createIndexCardsForProject();
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        //when
        ResultResponse resultResponse = this.deleteProjectService.deleteProject(this.user.getUsername(), project.getId());

        //then
        project = this.projectRepo.findProjectByProjectId(project.getId());
        user = this.userRepo.findByUsername(this.user.getUsername()).get();
        indexCard = this.indexCardRepo.findIndexCardByIndexcardId(indexCard.getId());
        assertEquals(WRONGFULLY_UNSUCCESSFUL, resultResponse.isSuccess(), true);
        assertEquals(WRONG_NUMBER_OF_ERRORS, resultResponse.getErrorMessages().isEmpty(), true);
        assertThat(user.getProjects().isEmpty()).isTrue();
        assertThat(project).isEqualTo(null);
        assertThat(indexCard).isEqualTo(null);
    }

    @Test
    public void deleteProjectWithWrongProjectIdTest() {
        // given
        createIndexCardsForProject();
        Project project = this.projectRepo.findProjectByName(this.projects.get(0).getName()).get(0);

        //when
        ResultResponse resultResponse
                = this.deleteProjectService.deleteProject(this.user.getUsername(), project.getId() + 1);

        //then
        project = this.projectRepo.findProjectByProjectId(project.getId());
        user = this.userRepo.findByUsername(this.user.getUsername()).get();
        indexCard = this.indexCardRepo.findIndexCardByIndexcardId(indexCard.getId());
        assertEquals(WRONGFULLY_SUCCESSFUL, resultResponse.isSuccess(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, resultResponse.getErrorMessages().isEmpty(), false);
        assertEquals(WRONG_NUMBER_OF_ERRORS, resultResponse.getErrorMessages().size(), 1);
        assertThat(resultResponse.getErrorMessages().get(0).getMessage())
                .isEqualTo(ErrorMessage.Project.PROJECT_NOT_FOUND);
        assertThat(user.getProjects().size()).isEqualTo(1);
        assertThat(project).isNotEqualTo(null);
        assertThat(indexCard).isNotEqualTo(null);
    }
}
