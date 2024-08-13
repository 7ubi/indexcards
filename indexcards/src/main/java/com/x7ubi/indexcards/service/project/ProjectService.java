package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import com.x7ubi.indexcards.response.project.ProjectResponse;
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
    public List<ProjectResponse> getUserProjects(String username) throws EntityNotFoundException {

        User user = getUser(username);

        List<Project> projects = user.getProjects();
        List<ProjectResponse> projectResponses = new ArrayList<>();

        for (Project project : projects) {
            List<IndexCardResponse> indexCardResponses = new ArrayList<>();
            for (IndexCard indexCard : project.getIndexCards()) {
                indexCardResponses.add(
                        new IndexCardResponse(
                                indexCard.getId(), Arrays.toString(indexCard.getQuestion()),
                                Arrays.toString(indexCard.getAnswer()), indexCard.getAssessment()));
            }
            projectResponses.add(new ProjectResponse(project.getId(), project.getName(), indexCardResponses));
        }

        logger.info("Found all projects from {}", user.getUsername());

        return projectResponses;
    }

    @Transactional
    public ProjectResponse getProject(long id) throws EntityNotFoundException {
        this.findGetProjectByIdError(id);


        Project project = this.projectRepo.findProjectByProjectId(id);

        logger.info("Found project {}", project.getName());

        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setId(project.getId());
        projectResponse.setName(project.getName());

        List<IndexCardResponse> indexCardResponses = new ArrayList<>();
        for (IndexCard indexCard : project.getIndexCards()) {
            indexCardResponses
                    .add(new IndexCardResponse(
                            indexCard.getId(), String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getQuestion()))),
                            String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getQuestion()))), indexCard.getAssessment()));
        }
        projectResponse.setIndexCardResponses(indexCardResponses);
        return projectResponse;
    }
}
