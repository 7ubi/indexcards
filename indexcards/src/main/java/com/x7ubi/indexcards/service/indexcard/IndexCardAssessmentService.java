package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.IndexCardAssessment;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import com.x7ubi.indexcards.response.common.MessageResponse;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class IndexCardAssessmentService {

    private final Logger logger = LoggerFactory.getLogger(IndexCardAssessmentService.class);
    private final IndexCardRepo indexCardRepo;

    private final IndexCardAssessmentRepo indexCardAssessmentRepo;

    public IndexCardAssessmentService(IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo) {
        this.indexCardRepo = indexCardRepo;
        this.indexCardAssessmentRepo = indexCardAssessmentRepo;
    }

    @Transactional
    public ResultResponse assessIndexCard(AssessmentRequest assessmentRequest) {
        ResultResponse resultResponse = new ResultResponse();

        resultResponse.setErrorMessages(this.findAssessIndexCardError(assessmentRequest));

        if(resultResponse.getErrorMessages().size() > 0) {
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

    private List<MessageResponse> findAssessIndexCardError(AssessmentRequest assessmentRequest) {
        List<MessageResponse> error = new ArrayList<>();

        if(!this.indexCardRepo.existsIndexCardByIndexcardId(assessmentRequest.getIndexCardId())) {
            error.add(new MessageResponse(ErrorMessage.IndexCards.INDEX_CARD_NOT_FOUND));
        }

        return error;
    }
}
