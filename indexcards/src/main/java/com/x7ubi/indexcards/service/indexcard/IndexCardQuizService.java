package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.IndexCardAssessment;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class IndexCardQuizService extends AbstractIndexCardService {

    private final Logger logger = LoggerFactory.getLogger(IndexCardQuizService.class);


    private final int maxIndexCardsPerQuiz = 5;

    public IndexCardQuizService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper, UserRepo userRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper, userRepo);
    }

    public List<IndexCardResponse> getIndexCardResponsesForQuiz(String username, Long id) throws EntityNotFoundException, UnauthorizedException {
        this.getProjectNotFoundError(id);

        User user = this.getUser(username);
        Project project = this.projectRepo.findProjectByProjectId(id);

        getProjectOwnerError(user, project);

        List<IndexCard> indexCards = new ArrayList<>(project.getIndexCards());
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

        return this.indexCardMapper.mapToResponses(indexCards.subList(0, Math.min(maxIndexCardsPerQuiz, indexCards.size())));
    }
}
