package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.common.MessageResponse;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateProjectService {

    private final Logger logger = LoggerFactory.getLogger(CreateProjectService.class);

    private final ProjectRepo projectRepo;

    private final UserRepo userRepo;

    public CreateProjectService(ProjectRepo projectRepo, UserRepo userRepo) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public ResultResponse createProject(String username, CreateProjectRequest createProjectRequest) {

        ResultResponse resultResponse = new ResultResponse();

        resultResponse.setErrorMessages(findCreateProjectErrors(username, createProjectRequest));

        if(resultResponse.getErrorMessages().size() > 0) {
            resultResponse.setSuccess(false);
            return resultResponse;
        }

        User user = this.userRepo.findByUsername(username).get();

        Project project = new Project();

        project.setName(createProjectRequest.getName());

        this.projectRepo.save(project);

        List<Project> userProjects = user.getProjects();
        userProjects.add(project);

        user.setProjects(userProjects);
        resultResponse.setSuccess(true);
        logger.info("Project was created successfully!");

        return resultResponse;
    }

    private List<MessageResponse> findCreateProjectErrors(
        String username,
        CreateProjectRequest createProjectRequest
    ) {
        List<MessageResponse> error = new ArrayList<>();

        if(!userRepo.existsByUsername(username)) {
            logger.error(ErrorMessage.Project.USERNAME_NOT_FOUND);
            error.add(new MessageResponse(ErrorMessage.Project.USERNAME_NOT_FOUND));
        } else {
            User user = this.userRepo.findByUsername(username).stream().findFirst().orElse(null);
            boolean projectNameExists = user.getProjects().stream().anyMatch(project ->
                project.getName().equals(createProjectRequest.getName())
            );

            if(projectNameExists) {
                logger.error(ErrorMessage.Project.PROJECT_NAME_EXISTS);
                error.add(new MessageResponse(ErrorMessage.Project.PROJECT_NAME_EXISTS));
            }
        }

        if(createProjectRequest.getName().length() > 100) {
            logger.error(ErrorMessage.Project.PROJECT_NAME_TOO_LONG);
            error.add(new MessageResponse(ErrorMessage.Project.PROJECT_NAME_TOO_LONG));
        }

        return error;
    }
}
