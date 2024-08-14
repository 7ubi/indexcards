package com.x7ubi.indexcards.service.project;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.mapper.ProjectMapper;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class DeleteProjectService extends AbstractProjectService {

    private final Logger logger = LoggerFactory.getLogger(DeleteProjectService.class);

    public DeleteProjectService(ProjectRepo projectRepo, UserRepo userRepo, IndexCardRepo indexCardRepo, ProjectMapper projectMapper) {
        super(projectRepo, userRepo, indexCardRepo, projectMapper);
    }

    @Transactional
    public void deleteProject(String username, Long id) throws EntityNotFoundException {
        findGetProjectByIdError(id);

        User user = getUser(username);
        Project project = projectRepo.findProjectByProjectId(id);
        user.getProjects().remove(project);
        userRepo.save(user);

        List<IndexCard> indexCardsOfProject = new ArrayList<>(project.getIndexCards());

        projectRepo.deleteProjectByProjectId(id);

        for (IndexCard indexCard : indexCardsOfProject) {
            indexCardRepo.deleteIndexCardByIndexcardId(indexCard.getIndexcardId());
        }

        logger.info("Project was deleted");
    }
}
