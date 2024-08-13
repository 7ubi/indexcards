package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;

@Service
public class CreateIndexCardService extends AbstractIndexCardService {

    private final Logger logger = LoggerFactory.getLogger(CreateIndexCardService.class);

    CreateIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo);
    }

    @Transactional
    public void createIndexCard(CreateIndexCardRequest createIndexCardRequest) throws EntityNotFoundException {
        this.getProjectNotFoundError(createIndexCardRequest.getProjectId());

        IndexCard indexCard
                = new IndexCard(StandardCharsets.UTF_8.encode(createIndexCardRequest.getQuestion()).array(),
                StandardCharsets.UTF_8.encode(createIndexCardRequest.getAnswer()).array());

        this.indexCardRepo.save(indexCard);

        Project project = this.projectRepo.findProjectByProjectId(createIndexCardRequest.getProjectId());
        project.getIndexCards().add(indexCard);
        this.projectRepo.save(project);

        logger.info("Index Card was created successfully!");
    }


}
