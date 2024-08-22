package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.IndexCardAssessment;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class IndexCardAssessmentService extends AbstractIndexCardService {

    private final Logger logger = LoggerFactory.getLogger(IndexCardAssessmentService.class);

    public IndexCardAssessmentService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper, UserRepo userRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper, userRepo);
    }

    @Transactional
    public void assessIndexCard(String username, AssessmentRequest assessmentRequest) throws EntityNotFoundException, UnauthorizedException {
        this.getIndexCardNotFoundError(assessmentRequest.getIndexCardId());

        User user = this.getUser(username);

        IndexCard indexCard = this.indexCardRepo.findIndexCardByIndexcardId(assessmentRequest.getIndexCardId());

        this.getProjectOwnerError(user, indexCard.getProject());

        IndexCardAssessment indexCardAssessment
                = new IndexCardAssessment(assessmentRequest.getAssessment(), LocalDateTime.now());
        this.indexCardAssessmentRepo.save(indexCardAssessment);
        indexCard.setAssessment(assessmentRequest.getAssessment());
        indexCard.getAssessmentHistory().add(indexCardAssessment);
        indexCard.getAssessmentHistory().forEach(item ->
                logger.info(String.format("%s, %s", item.getAssessment().toString(), item.getDate().toString())));
        this.indexCardRepo.save(indexCard);
    }
}
