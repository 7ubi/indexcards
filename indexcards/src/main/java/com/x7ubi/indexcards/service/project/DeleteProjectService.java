package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeleteProjectService extends AbstractProjectService {

    private final Logger logger = LoggerFactory.getLogger(DeleteProjectService.class);

    public DeleteProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo) {
        super(projectRepo, userRepo, indexCardRepo);
    }

    @Transactional
    public ResultResponse deleteProject(String username, Long id) {
        ResultResponse response = new ResultResponse();

        response.setErrorMessages(findGetProjectByIdError(id));

        if (!response.getErrorMessages().isEmpty()) {
            response.setSuccess(false);
            return response;
        }

        User user = userRepo.findByUsername(username).get();
        Project project = projectRepo.findProjectByProjectId(id);
        user.getProjects().remove(project);
        userRepo.save(user);

        List<IndexCard> indexCardsOfProject = new ArrayList<>(project.getIndexCards());

        projectRepo.deleteProjectByProjectId(id);

        for(IndexCard indexCard: indexCardsOfProject) {
            indexCardRepo.deleteIndexCardByIndexcardId(indexCard.getIndexcardId());
        }

        response.setSuccess(true);
        logger.info("Project was deleted");

        return response;
    }
}
