package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.project.CreateProjectRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class CreateProjectService extends AbstractProjectService {

    private final Logger logger = LoggerFactory.getLogger(CreateProjectService.class);

    public CreateProjectService(ProjectRepo projectRepo, UserRepo userRepo) {
        super(projectRepo, userRepo);
    }

    @Transactional
    public ResultResponse createProject(String username, CreateProjectRequest createProjectRequest) {

        ResultResponse resultResponse = new ResultResponse();

        resultResponse.setErrorMessages(getUserExists(username));

        if(resultResponse.getErrorMessages().size() == 0) {
            resultResponse.getErrorMessages().addAll(getProjectError(createProjectRequest, username));
        }

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
}
