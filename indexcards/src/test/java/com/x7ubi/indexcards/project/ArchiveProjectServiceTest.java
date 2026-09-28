package com.x7ubi.indexcards.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest()
@TestPropertySource(properties = {
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=USER"
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ArchiveProjectServiceTest extends ProjectTestConfig {

    private final LocalDate today = LocalDate.now();

    private Project project() {
        return this.projectRepo.findProjectByProjectId(this.projects.getFirst().getId());
    }

    private void setExamDate(LocalDate examDate) {
        Project project = project();
        project.setExamDate(examDate);
        this.projectRepo.save(project);
    }

    @Test
    public void archiveProjectTest() throws EntityNotFoundException, UnauthorizedException {
        // when
        this.archiveProjectService.archiveProject(user.getUsername(), project().getId());

        // then
        Assertions.assertTrue(project().isArchived());
    }

    @Test
    public void unarchiveProjectTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        this.archiveProjectService.archiveProject(user.getUsername(), project().getId());

        // when
        this.archiveProjectService.unarchiveProject(user.getUsername(), project().getId());

        // then
        Assertions.assertFalse(project().isArchived());
    }

    @Test
    public void archiveProjectWithUnauthorizedUserTest() {
        // when
        UnauthorizedException unauthorizedException = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.archiveProjectService.archiveProject(user2.getUsername(), project().getId()));

        // then
        Assertions.assertEquals(ErrorMessage.Project.USER_NOT_PROJECT_OWNER, unauthorizedException.getMessage());
        Assertions.assertFalse(project().isArchived());
    }

    @Test
    public void archiveNonexistentProjectTest() {
        // when
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.archiveProjectService.archiveProject(user.getUsername(), project().getId() + 1));

        // then
        Assertions.assertEquals(ErrorMessage.Project.PROJECT_NOT_FOUND, entityNotFoundException.getMessage());
    }

    @Test
    public void autoArchivePastExamProjectTest() {
        // given
        LocalDate examDate = today.minusDays(1);
        setExamDate(examDate);

        // when
        int archived = this.archiveProjectService.autoArchivePastExamProjects(today);

        // then
        Assertions.assertEquals(1, archived);
        Assertions.assertTrue(project().isArchived());
        Assertions.assertEquals(examDate, project().getAutoArchivedExamDate());
    }

    @Test
    public void autoArchiveIgnoresTodayFutureAndMissingExamDateTest() {
        for (LocalDate examDate : new LocalDate[]{today, today.plusDays(3), null}) {
            // given
            setExamDate(examDate);

            // when
            int archived = this.archiveProjectService.autoArchivePastExamProjects(today);

            // then
            Assertions.assertEquals(0, archived);
            Assertions.assertFalse(project().isArchived());
        }
    }

    @Test
    public void autoArchiveDoesNotReArchiveAfterUnarchiveTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        setExamDate(today.minusDays(1));
        this.archiveProjectService.autoArchivePastExamProjects(today);
        this.archiveProjectService.unarchiveProject(user.getUsername(), project().getId());

        // when
        int archived = this.archiveProjectService.autoArchivePastExamProjects(today.plusDays(1));

        // then
        Assertions.assertEquals(0, archived);
        Assertions.assertFalse(project().isArchived());
    }

    @Test
    public void autoArchiveReArchivesAfterExamDateChangeTest() throws EntityNotFoundException, UnauthorizedException {
        // given
        setExamDate(today.minusDays(5));
        this.archiveProjectService.autoArchivePastExamProjects(today);
        this.archiveProjectService.unarchiveProject(user.getUsername(), project().getId());
        LocalDate newExamDate = today.minusDays(1);
        setExamDate(newExamDate);

        // when
        int archived = this.archiveProjectService.autoArchivePastExamProjects(today);

        // then
        Assertions.assertEquals(1, archived);
        Assertions.assertTrue(project().isArchived());
        Assertions.assertEquals(newExamDate, project().getAutoArchivedExamDate());
    }
}
