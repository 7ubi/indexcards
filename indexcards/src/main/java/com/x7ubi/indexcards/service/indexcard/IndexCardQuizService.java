package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.IndexCardAssessment;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class IndexCardQuizService extends AbstractIndexCardService {

    private final Logger logger = LoggerFactory.getLogger(IndexCardQuizService.class);


    private final int maxIndexCardsPerQuiz = 5;

    public IndexCardQuizService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo);
    }

    public List<IndexCardResponse> getIndexCardResponsesForQuiz(Long id) throws EntityNotFoundException {
        this.getProjectNotFoundError(id);

        List<IndexCard> indexCards = new ArrayList<>(this.projectRepo.findProjectByProjectId(id).getIndexCards());
        indexCards.sort(Comparator.comparing(IndexCard::getAssessment).thenComparing((o1, o2) -> {
            List<IndexCardAssessment> indexCardAssessments1 = new ArrayList<>(o1.getAssessmentHistory());
            List<IndexCardAssessment> indexCardAssessments2 = new ArrayList<>(o2.getAssessmentHistory());

            if (indexCardAssessments1.isEmpty() || indexCardAssessments2.isEmpty()) {
                return o1.getId().compareTo(o2.getId());
            }

            LocalDateTime date1 = indexCardAssessments1.get(indexCardAssessments1.size() - 1).getDate();
            LocalDateTime date2 = indexCardAssessments2.get(indexCardAssessments2.size() - 1).getDate();

            return date1.compareTo(date2);
        }));

        List<IndexCardResponse> indexCardResponses = new ArrayList<>();

        for (int i = 0; i < maxIndexCardsPerQuiz; i++) {
            if (i == indexCards.size())
                break;
            IndexCard indexCard = indexCards.get(i);
            indexCardResponses.add(new IndexCardResponse(
                    indexCard.getId(), String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getQuestion()))),
                    String.valueOf(StandardCharsets.UTF_8.decode(ByteBuffer.wrap(indexCard.getAnswer()))), indexCard.getAssessment()));
        }

        return indexCardResponses;
    }
}
