package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.mapper.ProjectMapper;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class AbstractProjectService {
    private final Logger logger = LoggerFactory.getLogger(AbstractProjectService.class);

    protected final ProjectRepo projectRepo;

    protected final UserRepo userRepo;
    protected final IndexCardRepo indexCardRepo;

    protected final ProjectMapper projectMapper;

    public AbstractProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo, ProjectMapper projectMapper) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
        this.indexCardRepo = indexCardRepo;
        this.projectMapper = projectMapper;
    }

    protected User getUser(String username) throws EntityNotFoundException {
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            logger.error(ErrorMessage.Project.USERNAME_NOT_FOUND);
            throw new EntityNotFoundException(ErrorMessage.Project.USERNAME_NOT_FOUND);
        }

        return userOptional.get();
    }

    protected void getProjectError(CreateProjectRequest createProjectRequest, User user) throws EntityCreationException {
        if (createProjectRequest.getName().length() > 100) {
            logger.error(ErrorMessage.Project.PROJECT_NAME_TOO_LONG);
            throw new EntityCreationException(ErrorMessage.Project.PROJECT_NAME_TOO_LONG);
        }

        boolean projectNameExists = user.getProjects().stream().anyMatch(project ->
                project.getName().equals(createProjectRequest.getName())
        );

        if (projectNameExists) {
            logger.error(ErrorMessage.Project.PROJECT_NAME_EXISTS);
            throw new EntityCreationException(ErrorMessage.Project.PROJECT_NAME_EXISTS);
        }
    }

    protected void findGetProjectByIdError(long id) throws EntityNotFoundException {
        if (!projectRepo.existsByProjectId(id)) {
            logger.error(ErrorMessage.Project.PROJECT_NOT_FOUND);
            throw new EntityNotFoundException(ErrorMessage.Project.PROJECT_NOT_FOUND);
        }
    }
}
