package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.common.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AbstractProjectService {
    private final Logger logger = LoggerFactory.getLogger(AbstractProjectService.class);

    protected final ProjectRepo projectRepo;

    protected final UserRepo userRepo;

    public AbstractProjectService(ProjectRepo projectRepo, UserRepo userRepo) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
    }

    protected List<MessageResponse> getUserExists(String username) {
        List<MessageResponse> error = new ArrayList<>();

        if(!userRepo.existsByUsername(username)) {
            logger.error(ErrorMessage.Project.USERNAME_NOT_FOUND);
            error.add(new MessageResponse(ErrorMessage.Project.USERNAME_NOT_FOUND));
        }

        return error;
    }

    protected List<MessageResponse> getProjectError(CreateProjectRequest createProjectRequest, String username) {
        List<MessageResponse> error = new ArrayList<>();

        if(createProjectRequest.getName().length() > 100) {
            logger.error(ErrorMessage.Project.PROJECT_NAME_TOO_LONG);
            error.add(new MessageResponse(ErrorMessage.Project.PROJECT_NAME_TOO_LONG));
        }

        User user = this.userRepo.findByUsername(username).stream().findFirst().orElse(null);
        assert user != null;
        boolean projectNameExists = user.getProjects().stream().anyMatch(project ->
                project.getName().equals(createProjectRequest.getName())
        );

        if(projectNameExists) {
            logger.error(ErrorMessage.Project.PROJECT_NAME_EXISTS);
            error.add(new MessageResponse(ErrorMessage.Project.PROJECT_NAME_EXISTS));
        }

        return error;
    }

    protected List<MessageResponse> findGetProjectByIdError(long id) {
        List<MessageResponse> error = new ArrayList<>();

        if(!projectRepo.existsByProjectId(id)) {
            logger.error(ErrorMessage.Project.PROJECT_NOT_FOUND);
            error.add(new MessageResponse(ErrorMessage.Project.PROJECT_NOT_FOUND));
        }

        return error;
    }
}
