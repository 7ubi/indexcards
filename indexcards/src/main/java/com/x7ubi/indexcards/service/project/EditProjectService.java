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
public class EditProjectService extends AbstractProjectService {
    Logger logger = LoggerFactory.getLogger(EditProjectService.class);

    public EditProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo, ProjectMapper projectMapper) {
        super(projectRepo, userRepo, indexCardRepo, projectMapper);
    }

    @Transactional
    public void editProject(CreateProjectRequest createProjectRequest, Long id, String username) throws EntityNotFoundException, EntityCreationException {
        findGetProjectByIdError(id);
        User user = getUser(username);
        getProjectError(createProjectRequest, user);

        Project project = projectRepo.findProjectByProjectId(id);
        project.setName(createProjectRequest.getName());
        projectRepo.save(project);

        logger.info("Project was edited");
    }
}
