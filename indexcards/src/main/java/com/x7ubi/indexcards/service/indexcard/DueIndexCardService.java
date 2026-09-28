package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.indexcard.DueIndexCardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class DueIndexCardService extends AbstractIndexCardService {

    private final Logger logger = LoggerFactory.getLogger(DueIndexCardService.class);

    public DueIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper, UserRepo userRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper, userRepo);
    }

    public List<DueIndexCardResponse> getDueIndexCards(String username) throws EntityNotFoundException {
        return getDueIndexCards(username, LocalDateTime.now());
    }

    /**
     * Returns every card across all of the user's non-archived projects that is due before the end of today
     * (server time), most overdue first. Filtering happens in memory rather than in a query so
     * never-scheduled cards ({@code NULL} due date) count as due via {@link IndexCard#getDueDate()}.
     */
    public List<DueIndexCardResponse> getDueIndexCards(String username, LocalDateTime now) throws EntityNotFoundException {
        User user = this.getUser(username);
        LocalDateTime startOfTomorrow = now.toLocalDate().plusDays(1).atStartOfDay();

        List<IndexCard> dueIndexCards = user.getProjects().stream()
                .filter(project -> !project.isArchived())
                .flatMap(project -> project.getIndexCards().stream())
                .filter(indexCard -> indexCard.getDueDate().isBefore(startOfTomorrow))
                .sorted(Comparator.comparing(IndexCard::getDueDate).thenComparing(IndexCard::getId))
                .toList();

        logger.info("Found {} due index cards for {}", dueIndexCards.size(), user.getUsername());

        return this.indexCardMapper.mapToDueResponses(dueIndexCards);
    }
}
