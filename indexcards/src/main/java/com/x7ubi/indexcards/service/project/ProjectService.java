package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.common.MessageResponse;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import com.x7ubi.indexcards.response.project.ProjectResponse;
import com.x7ubi.indexcards.response.project.UserProjectsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    private Logger logger = LoggerFactory.getLogger(ProjectService.class);

    private final ProjectRepo projectRepo;

    private final UserRepo userRepo;

    private final JwtUtils jwtUtils;

    public ProjectService(ProjectRepo projectRepo, UserRepo userRepo, JwtUtils jwtUtils) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
        this.jwtUtils = jwtUtils;
    }


    public UserProjectsResponse getUserProjects(String authorization) {

        UserProjectsResponse userProjectResponse = new UserProjectsResponse();

        userProjectResponse.setErrorMessages(findGetProjectErrors(authorization));

        if(userProjectResponse.getErrorMessages().size() > 0) {
            return userProjectResponse;
        }

        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        User user = this.userRepo.findByUsername(username).get();

        List<Project> projects = user.getProjects();

        userProjectResponse.setProjectResponses(new ArrayList<>());
        userProjectResponse.setSuccess(true);

        for(Project project: projects) {
            List<IndexCardResponse> indexCardResponses = new ArrayList<>();
            for(IndexCard indexCard: project.getIndexCards()) {
                indexCardResponses.add(new IndexCardResponse(indexCard.getName()));
            }
            userProjectResponse.getProjectResponses()
                    .add(new ProjectResponse(project.getName(), indexCardResponses));
        }

        logger.info("Found all projects from {}", user.getUsername());

        return userProjectResponse;
    }

    private List<MessageResponse> findGetProjectErrors(String authorization) {
        List<MessageResponse> errors = new ArrayList<>();

        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        if(!userRepo.existsByUsername(username)) {
            logger.error(ErrorMessage.Project.USERNAME_NOT_FOUND);
            errors.add(new MessageResponse(ErrorMessage.Project.USERNAME_NOT_FOUND));
        }

        return errors;
    }
}
