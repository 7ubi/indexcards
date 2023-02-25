package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.response.common.MessageResponse;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateIndexCardService {

    private final Logger logger = LoggerFactory.getLogger(CreateIndexCardService.class);

    private final ProjectRepo projectRepo;

    private final IndexCardRepo indexCardRepo;

    CreateIndexCardService(ProjectRepo projectRepo, IndexCardRepo indexCardRepo) {
        this.projectRepo = projectRepo;
        this.indexCardRepo = indexCardRepo;
    }

    @Transactional
    public ResultResponse createIndexCard(CreateIndexCardRequest createIndexCardRequest) {
        ResultResponse resultResponse = new ResultResponse();

        resultResponse.setErrorMessages(
            this.findCreateIndexCardErrors(createIndexCardRequest)
        );

        if(resultResponse.getErrorMessages().size() > 0) {
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

    private List<MessageResponse> findCreateIndexCardErrors(CreateIndexCardRequest createIndexCardRequest) {
        List<MessageResponse> error = new ArrayList<>();

        if(!projectRepo.existsById(createIndexCardRequest.getProjectId())) {
            logger.error(ErrorMessage.IndexCards.PROJECT_NOT_FOUND);
            error.add(new MessageResponse(ErrorMessage.IndexCards.PROJECT_NOT_FOUND));
        }

        if(createIndexCardRequest.getQuestion().length() > 500) {
            logger.error(ErrorMessage.IndexCards.INDEXCARD_QUESTION_TOO_LONG);
            error.add(new MessageResponse(ErrorMessage.IndexCards.INDEXCARD_QUESTION_TOO_LONG));
        }

        if(createIndexCardRequest.getAnswer().length() > 500) {
            logger.error(ErrorMessage.IndexCards.INDEXCARD_ANSWER_TOO_LONG);
            error.add(new MessageResponse(ErrorMessage.IndexCards.INDEXCARD_ANSWER_TOO_LONG));
        }

        return error;
    }
}
