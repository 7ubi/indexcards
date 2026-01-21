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
import com.x7ubi.indexcards.request.indexcard.IndexCardCsvImportRequest;
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


    public void importIndexCardsFromCsv(String username, IndexCardCsvImportRequest indexCardCsvImportRequest) throws EntityNotFoundException, UnauthorizedException {
        User user = getUser(username);

        Project project = this.projectRepo.findProjectByProjectId(indexCardCsvImportRequest.getProjectId());
        getProjectOwnerError(user, project);
        String[] lines = indexCardCsvImportRequest.getCsv().split("\\r?\\n");
        for (String line : lines) {
            String[] values = parseCsvLine(line);

            if(values.length < 2) {
                logger.warn("Invalid CSV line format: {}. Skipping line.", line);
                continue;
            }

            String question = values[0].trim();
            String answer = values[1].trim();


            IndexCard indexCard = new IndexCard();
            indexCard.setProject(project);
            indexCard.setQuestion(java.nio.charset.StandardCharsets.UTF_8.encode(question).array());
            indexCard.setAnswer(java.nio.charset.StandardCharsets.UTF_8.encode(answer).array());

            this.indexCardRepo.save(indexCard);
        }

        logger.info("CSV import completed successfully!");
    }

    private static String[] parseCsvLine(String line) {
        if(line == null || line.isEmpty()) {
            return new String[0];
        }

        if (line.charAt(0) == '"' && line.charAt(line.length() - 1) != '"') {
            line = line.substring(1, line.length() - 1);
            return line.split("\",", -1);
        } else if (line.charAt(0) == '"' && line.charAt(line.length() - 1) == '"') {
            line = line.substring(1, line.length() - 1);
            return line.split("\",\"", -1);
        } else if (line.charAt(0) != '"' && line.charAt(line.length() - 1) == '"') {
            line = line.substring(0, line.length() - 1);
            return line.split(",\"", -1);
        }else {
            return line.split(",", -1);
        }
    }
}
