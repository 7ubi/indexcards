package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import com.x7ubi.indexcards.response.project.ProjectResponse;
import com.x7ubi.indexcards.response.project.UserProjectResponse;
import com.x7ubi.indexcards.response.project.UserProjectsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProjectService extends AbstractProjectService {

    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    public ProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo) {
        super(projectRepo, userRepo, indexCardRepo);
    }

    @Transactional
    public UserProjectsResponse getUserProjects(String username) {

        UserProjectsResponse userProjectResponse = new UserProjectsResponse();

        userProjectResponse.setErrorMessages(getUserExists(username));

        if (!userProjectResponse.getErrorMessages().isEmpty()) {
            return userProjectResponse;
        }

        User user = this.userRepo.findByUsername(username).get();

        List<Project> projects = user.getProjects();

        userProjectResponse.setProjectResponses(new ArrayList<>());
        userProjectResponse.setSuccess(true);

        for(Project project: projects) {
            List<IndexCardResponse> indexCardResponses = new ArrayList<>();
            for(IndexCard indexCard: project.getIndexCards()) {
                indexCardResponses.add(
                    new IndexCardResponse(
                            indexCard.getId(), Arrays.toString(indexCard.getQuestion()),
                            Arrays.toString(indexCard.getAnswer()), indexCard.getAssessment()));
            }
            userProjectResponse.getProjectResponses()
                    .add(new ProjectResponse(project.getId(), project.getName(), indexCardResponses));
        }

        logger.info("Found all projects from {}", user.getUsername());

        return userProjectResponse;
    }

    @Transactional
    public UserProjectResponse getProject(long id) {
    UserProjectResponse userProjectResponse = new UserProjectResponse();

        userProjectResponse.setErrorMessages(this.findGetProjectByIdError(id));

        if (!userProjectResponse.getErrorMessages().isEmpty()) {
            userProjectResponse.setSuccess(false);
            return userProjectResponse;
        }

        Project project = this.projectRepo.findProjectByProjectId(id);

        logger.info("Found project {}", project.getName());

        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setId(project.getId());
        projectResponse.setName(project.getName());

        List<IndexCardResponse> indexCardResponses = new ArrayList<>();
        for(IndexCard indexCard: project.getIndexCards()) {
            indexCardResponses
                .add(new IndexCardResponse(
                        indexCard.getId(), String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getQuestion()))),
                        String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getQuestion()))), indexCard.getAssessment()));
        }
        projectResponse.setIndexCardResponses(indexCardResponses);
        userProjectResponse.setSuccess(true);
        userProjectResponse.setProjectResponse(projectResponse);

        return userProjectResponse;
    }
}
