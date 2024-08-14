package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.mapper.ProjectMapper;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.project.ProjectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProjectService extends AbstractProjectService {

    private final Logger logger = LoggerFactory.getLogger(ProjectService.class);

    public ProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo, ProjectMapper projectMapper) {
        super(projectRepo, userRepo, indexCardRepo, projectMapper);
    }

    @Transactional
    public List<ProjectResponse> getUserProjects(String username) throws EntityNotFoundException {

        User user = getUser(username);

        List<Project> projects = user.getProjects();

        logger.info("Found all projects from {}", user.getUsername());

        return this.projectMapper.mapToResponse(projects);
    }

    @Transactional
    public ProjectResponse getProject(long id) throws EntityNotFoundException {
        this.findGetProjectByIdError(id);
        
        Project project = this.projectRepo.findProjectByProjectId(id);

        logger.info("Found project {}", project.getName());

        return this.projectMapper.mapToResponse(project);
    }
}
