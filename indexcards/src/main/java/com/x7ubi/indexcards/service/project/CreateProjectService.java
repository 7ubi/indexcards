package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.mapper.ProjectMapper;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CreateProjectService extends AbstractProjectService {

    private final Logger logger = LoggerFactory.getLogger(CreateProjectService.class);

    public CreateProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo, ProjectMapper projectMapper) {
        super(projectRepo, userRepo, indexCardRepo, projectMapper);
    }

    @Transactional
    public void createProject(String username, CreateProjectRequest createProjectRequest) throws EntityNotFoundException, EntityCreationException {

        User user = getUser(username);
        getProjectError(createProjectRequest, user);

        Project project = this.projectMapper.mapRequestToProject(createProjectRequest);
        project.setUser(user);
        this.projectRepo.save(project);

        logger.info("Project was created successfully!");
    }
}
