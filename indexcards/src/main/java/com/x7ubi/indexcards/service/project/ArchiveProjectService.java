package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.mapper.ProjectMapper;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class ArchiveProjectService extends AbstractProjectService {

    private final Logger logger = LoggerFactory.getLogger(ArchiveProjectService.class);

    public ArchiveProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo, ProjectMapper projectMapper) {
        super(projectRepo, userRepo, indexCardRepo, projectMapper);
    }

    @Transactional
    public void archiveProject(String username, Long id) throws EntityNotFoundException, UnauthorizedException {
        setArchived(username, id, true);
        logger.info("Project was archived");
    }

    /**
     * Unarchiving leaves {@link Project#getAutoArchivedExamDate()} untouched, so a project whose exam date has passed is
     * not archived again automatically until its exam date changes.
     */
    @Transactional
    public void unarchiveProject(String username, Long id) throws EntityNotFoundException, UnauthorizedException {
        setArchived(username, id, false);
        logger.info("Project was unarchived");
    }

    /**
     * Archives every project whose exam date lies before {@code today}, once per exam date.
     *
     * @return the number of projects archived
     */
    @Transactional
    public int autoArchivePastExamProjects(LocalDate today) {
        List<Project> projects = projectRepo.findAutoArchiveCandidates(today);

        for (Project project : projects) {
            project.setArchived(true);
            project.setAutoArchivedExamDate(project.getExamDate());
        }
        projectRepo.saveAll(projects);

        logger.info("Auto-archived {} projects with a past exam date", projects.size());
        return projects.size();
    }

    private void setArchived(String username, Long id, boolean archived) throws EntityNotFoundException, UnauthorizedException {
        findGetProjectByIdError(id);

        User user = getUser(username);
        Project project = projectRepo.findProjectByProjectId(id);
        getUserProjectOwnerError(user, project);

        project.setArchived(archived);
        projectRepo.save(project);
    }
}
