package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.IndexCardAssessment;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class IndexCardAssessmentService extends AbstractIndexCardService {

    private final Logger logger = LoggerFactory.getLogger(IndexCardAssessmentService.class);

    public IndexCardAssessmentService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo);
    }

    @Transactional
    public ResultResponse assessIndexCard(AssessmentRequest assessmentRequest) {
        ResultResponse resultResponse = new ResultResponse();

        resultResponse.setErrorMessages(this.getIndexCardNotFoundError(assessmentRequest.getIndexCardId()));

        if (!resultResponse.getErrorMessages().isEmpty()) {
            resultResponse.setSuccess(false);
            return resultResponse;
        }

        IndexCard indexCard = this.indexCardRepo.findIndexCardByIndexcardId(assessmentRequest.getIndexCardId());
        IndexCardAssessment indexCardAssessment
                = new IndexCardAssessment(assessmentRequest.getAssessment(), LocalDateTime.now());
        this.indexCardAssessmentRepo.save(indexCardAssessment);
        indexCard.setAssessment(assessmentRequest.getAssessment());
        indexCard.getAssessmentHistory().add(indexCardAssessment);
        indexCard.getAssessmentHistory().forEach(item ->
                logger.info(String.format("%s, %s", item.getAssessment().toString(), item.getDate().toString())));
        this.indexCardRepo.save(indexCard);

        resultResponse.setSuccess(true);
        return resultResponse;
    }
}
