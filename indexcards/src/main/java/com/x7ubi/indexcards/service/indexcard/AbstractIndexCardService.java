package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class AbstractIndexCardService {
    private final Logger logger = LoggerFactory.getLogger(AbstractIndexCardService.class);

    protected final ProjectRepo projectRepo;
    protected final IndexCardRepo indexCardRepo;

    protected final IndexCardAssessmentRepo indexCardAssessmentRepo;

    protected final IndexCardMapper indexCardMapper;

    private final UserRepo userRepo;

    public AbstractIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper, UserRepo userRepo) {
        this.projectRepo = projectRepo;
        this.indexCardRepo = indexCardRepo;
        this.indexCardAssessmentRepo = indexCardAssessmentRepo;
        this.indexCardMapper = indexCardMapper;
        this.userRepo = userRepo;
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

    protected void getProjectOwnerError(User user, Project project) throws UnauthorizedException {
        if (!Objects.equals(project.getUser().getId(), user.getId())) {
            logger.error(ErrorMessage.Project.USER_NOT_PROJECT_OWNER);
            throw new UnauthorizedException(ErrorMessage.Project.USER_NOT_PROJECT_OWNER);
        }
    }

    protected User getUser(String username) throws EntityNotFoundException {
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isEmpty()) {
            logger.error(ErrorMessage.Project.USERNAME_NOT_FOUND);
            throw new EntityNotFoundException(ErrorMessage.Project.USERNAME_NOT_FOUND);
        }

        return userOptional.get();
    }

    @Deprecated
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
