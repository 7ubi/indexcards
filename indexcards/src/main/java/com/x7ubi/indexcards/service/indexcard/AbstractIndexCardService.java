package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import com.x7ubi.indexcards.response.common.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AbstractIndexCardService {
    private final Logger logger = LoggerFactory.getLogger(AbstractIndexCardService.class);

    protected final ProjectRepo projectRepo;
    protected final IndexCardRepo indexCardRepo;

    protected final IndexCardAssessmentRepo indexCardAssessmentRepo;

    public AbstractIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo) {
        this.projectRepo = projectRepo;
        this.indexCardRepo = indexCardRepo;
        this.indexCardAssessmentRepo = indexCardAssessmentRepo;
    }

    protected List<MessageResponse> getProjectNotFoundError(long id) {
        List<MessageResponse> error = new ArrayList<>();

        if (!projectRepo.existsByProjectId(id)) {
            logger.error(ErrorMessage.Project.PROJECT_NOT_FOUND);
            error.add(new MessageResponse(ErrorMessage.Project.PROJECT_NOT_FOUND));
        }

        return error;
    }

    protected List<MessageResponse> getIndexCardNotFoundError(Long id) {
        List<MessageResponse> error = new ArrayList<>();

        if (!this.indexCardRepo.existsIndexCardByIndexcardId(id)) {
            error.add(new MessageResponse(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND));
        }

        return error;
    }

    protected List<MessageResponse> getIndexCardCreationErrors(CreateIndexCardRequest createIndexCardRequest) {
        List<MessageResponse> error = new ArrayList<>();

        if (createIndexCardRequest.getQuestion().length() > 500) {
            logger.error(ErrorMessage.IndexCards.INDEXCARD_QUESTION_TOO_LONG);
            error.add(new MessageResponse(ErrorMessage.IndexCards.INDEXCARD_QUESTION_TOO_LONG));
        }

        if (createIndexCardRequest.getAnswer().length() > 500) {
            logger.error(ErrorMessage.IndexCards.INDEXCARD_ANSWER_TOO_LONG);
            error.add(new MessageResponse(ErrorMessage.IndexCards.INDEXCARD_ANSWER_TOO_LONG));
        }

        return error;
    }
}
