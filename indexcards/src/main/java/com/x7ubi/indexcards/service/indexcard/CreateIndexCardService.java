package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CreateIndexCardService extends AbstractIndexCardService {

    private final Logger logger = LoggerFactory.getLogger(CreateIndexCardService.class);

    CreateIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper, UserRepo userRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper, userRepo);
    }

    @Transactional
    public void createIndexCard(String username, CreateIndexCardRequest createIndexCardRequest) throws EntityNotFoundException, UnauthorizedException {
        this.getProjectNotFoundError(createIndexCardRequest.getProjectId());
        User user = getUser(username);

        IndexCard indexCard = this.indexCardMapper.mapRequestToIndexCard(createIndexCardRequest);
        Project project = this.projectRepo.findProjectByProjectId(createIndexCardRequest.getProjectId());
        getProjectOwnerError(user, project);

        indexCard.setProject(project);
        this.indexCardRepo.save(indexCard);

        logger.info("Index Card was created successfully!");
    }


}
