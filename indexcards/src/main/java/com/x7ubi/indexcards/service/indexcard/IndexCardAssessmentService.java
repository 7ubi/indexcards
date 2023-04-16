package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.request.indexcard.AssessmentRequest;
import com.x7ubi.indexcards.response.common.MessageResponse;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class IndexCardAssessmentService {
    private final IndexCardRepo indexCardRepo;

    public IndexCardAssessmentService(IndexCardRepo indexCardRepo) {
        this.indexCardRepo = indexCardRepo;
    }

    public ResultResponse assessIndexCard(AssessmentRequest assessmentRequest) {
        ResultResponse resultResponse = new ResultResponse();

        resultResponse.setErrorMessages(this.findAssessIndexCardError(assessmentRequest));

        if(resultResponse.getErrorMessages().size() > 0) {
            resultResponse.setSuccess(false);
            return resultResponse;
        }

        IndexCard indexCard = this.indexCardRepo.findIndexCardByIndexcardId(assessmentRequest.getIndexCardId());
        indexCard.setAssessment(assessmentRequest.getAssessment());
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
