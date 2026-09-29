package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
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
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardsRequest;
import com.x7ubi.indexcards.request.indexcard.IndexCardContentRequest;
import com.x7ubi.indexcards.request.indexcard.IndexCardCsvImportRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateIndexCardService extends AbstractIndexCardService {

    public static final int MAX_CARDS_PER_REQUEST = 100;

    private final Logger logger = LoggerFactory.getLogger(CreateIndexCardService.class);

    private record QuestionAnswer(String question, String answer) {}

    CreateIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper, UserRepo userRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper, userRepo);
    }

    @Transactional
    public void createIndexCard(String username, CreateIndexCardRequest createIndexCardRequest) throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        this.getProjectNotFoundError(createIndexCardRequest.getProjectId());
        User user = getUser(username);

        IndexCard indexCard = this.indexCardMapper.mapRequestToIndexCard(createIndexCardRequest);
        Project project = this.projectRepo.findProjectByProjectId(createIndexCardRequest.getProjectId());
        getProjectOwnerError(user, project);
        getProjectArchivedError(project);

        indexCard.setProject(project);
        this.indexCardRepo.save(indexCard);

        logger.info("Index Card was created successfully!");
    }


    public void importIndexCardsFromCsv(String username, IndexCardCsvImportRequest indexCardCsvImportRequest) throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        this.getProjectNotFoundError(indexCardCsvImportRequest.getProjectId());
        User user = getUser(username);

        Project project = this.projectRepo.findProjectByProjectId(indexCardCsvImportRequest.getProjectId());
        getProjectOwnerError(user, project);
        getProjectArchivedError(project);
        String[] lines = indexCardCsvImportRequest.getCsv().split("\\r?\\n");
        List<QuestionAnswer> questionAnswers = new ArrayList<>();
        for (String line : lines) {
            String[] values = parseCsvLine(line);

            if(values.length < 2) {
                logger.warn("Invalid CSV line format: {}. Skipping line.", line);
                continue;
            }

            questionAnswers.add(new QuestionAnswer(values[0].trim(), values[1].trim()));
        }
        saveNewIndexCards(project, questionAnswers);

        logger.info("CSV import completed successfully!");
    }

    /**
     * Saves several cards at once, e.g. the reviewed AI suggestions. All cards are validated before any is saved, so a
     * request is saved completely or not at all.
     */
    @Transactional
    public void createIndexCards(String username, CreateIndexCardsRequest request)
            throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        this.getProjectNotFoundError(request.getProjectId());
        User user = getUser(username);

        Project project = this.projectRepo.findProjectByProjectId(request.getProjectId());
        getProjectOwnerError(user, project);
        getProjectArchivedError(project);

        List<IndexCardContentRequest> cards = request.getCards() == null ? List.of() : request.getCards();
        if (cards.isEmpty()) {
            logger.error(ErrorMessage.IndexCards.INDEXCARD_EMPTY);
            throw new EntityCreationException(ErrorMessage.IndexCards.INDEXCARD_EMPTY);
        }
        if (cards.size() > MAX_CARDS_PER_REQUEST) {
            logger.error(ErrorMessage.IndexCards.TOO_MANY_INDEXCARDS);
            throw new EntityCreationException(ErrorMessage.IndexCards.TOO_MANY_INDEXCARDS);
        }

        List<QuestionAnswer> questionAnswers = new ArrayList<>();
        for (IndexCardContentRequest card : cards) {
            String question = card.getQuestion() == null ? "" : card.getQuestion().strip();
            String answer = card.getAnswer() == null ? "" : card.getAnswer().strip();
            if (question.isEmpty() || answer.isEmpty()) {
                logger.error(ErrorMessage.IndexCards.INDEXCARD_EMPTY);
                throw new EntityCreationException(ErrorMessage.IndexCards.INDEXCARD_EMPTY);
            }
            questionAnswers.add(new QuestionAnswer(question, answer));
        }

        saveNewIndexCards(project, questionAnswers);
        logger.info("{} index cards were created", questionAnswers.size());
    }

    private void saveNewIndexCards(Project project, List<QuestionAnswer> questionAnswers) {
        for (QuestionAnswer questionAnswer : questionAnswers) {
            IndexCard indexCard = new IndexCard();
            indexCard.setProject(project);
            // Same encoding as the CSV import always used, so existing lookups by question bytes keep matching.
            indexCard.setQuestion(StandardCharsets.UTF_8.encode(questionAnswer.question()).array());
            indexCard.setAnswer(StandardCharsets.UTF_8.encode(questionAnswer.answer()).array());
            this.indexCardRepo.save(indexCard);
        }
    }

    private static String[] parseCsvLine(String line) {
        if(line == null || line.isEmpty()) {
            return new String[0];
        }

        String[] values;
        if (line.charAt(0) == '"' && line.charAt(line.length() - 1) != '"') {
            values = line.substring(1, line.length() - 1).split("\",", -1);
        } else if (line.charAt(0) == '"' && line.charAt(line.length() - 1) == '"') {
            values = line.substring(1, line.length() - 1).split("\",\"", -1);
        } else if (line.charAt(0) != '"' && line.charAt(line.length() - 1) == '"') {
            values = line.substring(0, line.length() - 1).split(",\"", -1);
        } else {
            values = line.split(",", -1);
        }

        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].replace("\"\"", "\"");
        }
        return values;
    }
}
