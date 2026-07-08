package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.mapper.ProjectMapper;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.service.indexcard.SpacedRepetitionScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class EditProjectService extends AbstractProjectService {
    Logger logger = LoggerFactory.getLogger(EditProjectService.class);

    public EditProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo, ProjectMapper projectMapper) {
        super(projectRepo, userRepo, indexCardRepo, projectMapper);
    }

    @Transactional
    public void editProject(CreateProjectRequest createProjectRequest, Long id, String username) throws EntityNotFoundException, EntityCreationException, UnauthorizedException {
        findGetProjectByIdError(id);
        User user = getUser(username);
        getProjectError(createProjectRequest, user, id);

        Project project = projectRepo.findProjectByProjectId(id);

        getUserProjectOwnerError(user, project);

        project.setName(createProjectRequest.getName());
        project.setExamDate(createProjectRequest.getExamDate());
        projectRepo.save(project);

        if (project.getExamDate() != null) {
            LocalDateTime now = LocalDateTime.now();
            project.getIndexCards().forEach(indexCard ->
                    SpacedRepetitionScheduler.applyExamDateCap(indexCard, now, project.getExamDate()));
            indexCardRepo.saveAll(project.getIndexCards());
        }

        logger.info("Project was edited");
    }
}
