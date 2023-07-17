package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.IndexCardAssessment;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.response.common.MessageResponse;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class IndexCardQuizService {

    private final Logger logger = LoggerFactory.getLogger(IndexCardQuizService.class);

    private final ProjectRepo projectRepo;

    private final int maxIndexCardsPerQuiz = 5;

    public IndexCardQuizService(ProjectRepo projectRepo) {
        this.projectRepo = projectRepo;
    }

    public IndexCardResponses getIndexCardResponsesForQuiz(Long id) {
        IndexCardResponses indexCardResponses = new IndexCardResponses();

        indexCardResponses.setErrorMessages(this.findGetIndexCardResponsesForQuiz(id));

        if (indexCardResponses.getErrorMessages().size() != 0) {
            indexCardResponses.setSuccess(false);
            return indexCardResponses;
        }

        List<IndexCard> indexCards = new ArrayList<>(this.projectRepo.findProjectByProjectId(id).getIndexCards());
        indexCards.sort(Comparator.comparing(IndexCard::getAssessment).thenComparing((o1, o2) -> {
            List<IndexCardAssessment> indexCardAssessments1 = new ArrayList<>(o1.getAssessmentHistory());
            List<IndexCardAssessment> indexCardAssessments2 = new ArrayList<>(o2.getAssessmentHistory());

            if(indexCardAssessments1.size() == 0 || indexCardAssessments2.size() == 0) {
                return o1.getId().compareTo(o2.getId());
            }

            LocalDateTime date1 = indexCardAssessments1.get(indexCardAssessments1.size() - 1).getDate();
            LocalDateTime date2 = indexCardAssessments2.get(indexCardAssessments2.size() - 1).getDate();

            return date1.compareTo(date2);
        }));
        indexCardResponses.setIndexCardResponses(new ArrayList<>());

        for (int i = 0; i < maxIndexCardsPerQuiz; i++) {
            if(i == indexCards.size())
                break;
            IndexCard indexCard = indexCards.get(i);
            indexCardResponses.getIndexCardResponses().add(new IndexCardResponse(
                    indexCard.getId(), indexCard.getQuestion(), indexCard.getAnswer(), indexCard.getAssessment()));
        }
        indexCardResponses.setSuccess(true);

        return indexCardResponses;
    }

    private List<MessageResponse> findGetIndexCardResponsesForQuiz(long id) {
        List<MessageResponse> error = new ArrayList<>();

        if (!projectRepo.existsByProjectId(id)) {
            logger.error(ErrorMessage.Project.PROJECT_NOT_FOUND);
            error.add(new MessageResponse(ErrorMessage.Project.PROJECT_NOT_FOUND));
        }

        return error;
    }
}
