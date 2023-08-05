package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class EditProjectService extends AbstractProjectService {
    Logger logger = LoggerFactory.getLogger(EditProjectService.class);

    public EditProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo) {
        super(projectRepo, userRepo, indexCardRepo);
    }

    @Transactional
    public ResultResponse editProject(CreateProjectRequest createProjectService, Long id, String username) {
        ResultResponse response = new ResultResponse();

        response.setErrorMessages(findGetProjectByIdError(id));
        response.getErrorMessages().addAll(getProjectError(createProjectService, username));

        if(response.getErrorMessages().size() > 0) {
            response.setSuccess(false);
            return response;
        }

        Project project = projectRepo.findProjectByProjectId(id);
        project.setName(createProjectService.getName());
        projectRepo.save(project);

        logger.info("Project was edited");

        response.setSuccess(true);
        return response;
    }
}
