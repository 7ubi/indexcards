package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    protected void getProjectNotFoundError(long id) throws EntityNotFoundException {

        if (!projectRepo.existsByProjectId(id)) {
            logger.error(ErrorMessage.Project.PROJECT_NOT_FOUND);
            throw new EntityNotFoundException(ErrorMessage.Project.PROJECT_NOT_FOUND);
        }
    }

    protected void getIndexCardNotFoundError(Long id) throws EntityNotFoundException {
        if (!this.indexCardRepo.existsIndexCardByIndexcardId(id)) {
            logger.error(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND);
            throw new EntityNotFoundException(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND);
        }
    }

    protected void getIndexCardCreationErrors(CreateIndexCardRequest createIndexCardRequest) throws EntityCreationException {
        if (createIndexCardRequest.getQuestion().length() > 500) {
            logger.error(ErrorMessage.IndexCards.INDEXCARD_QUESTION_TOO_LONG);
            throw new EntityCreationException(ErrorMessage.IndexCards.INDEXCARD_QUESTION_TOO_LONG);
        }

        if (createIndexCardRequest.getAnswer().length() > 500) {
            logger.error(ErrorMessage.IndexCards.INDEXCARD_ANSWER_TOO_LONG);
            throw new EntityCreationException(ErrorMessage.IndexCards.INDEXCARD_ANSWER_TOO_LONG);
        }
    }
}
