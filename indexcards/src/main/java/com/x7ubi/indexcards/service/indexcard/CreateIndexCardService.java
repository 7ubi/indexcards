package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CreateIndexCardService extends AbstractIndexCardService {

    private final Logger logger = LoggerFactory.getLogger(CreateIndexCardService.class);

    CreateIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo);
    }

    @Transactional
    public ResultResponse createIndexCard(CreateIndexCardRequest createIndexCardRequest) {
        ResultResponse resultResponse = new ResultResponse();

        resultResponse.setErrorMessages(
                this.getProjectNotFoundError(createIndexCardRequest.getProjectId())
        );
        if (resultResponse.getErrorMessages().isEmpty()) {
            resultResponse.getErrorMessages().addAll(this.getIndexCardCreationErrors(createIndexCardRequest));
        }

        if (!resultResponse.getErrorMessages().isEmpty()) {
            return resultResponse;
        }

        IndexCard indexCard
            = new IndexCard(createIndexCardRequest.getQuestion(), createIndexCardRequest.getAnswer());

        this.indexCardRepo.save(indexCard);

        Project project = this.projectRepo.findProjectByProjectId(createIndexCardRequest.getProjectId());
        project.getIndexCards().add(indexCard);
        this.projectRepo.save(project);

        logger.info("Index Card was created successfully!");

        resultResponse.setSuccess(true);

        return resultResponse;
    }


}
