package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.EditIndexCardRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EditIndexCardService extends AbstractIndexCardService {

    public EditIndexCardService(ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo);
    }

    public ResultResponse editIndexCard(EditIndexCardRequest editIndexCardRequest) {
        ResultResponse response = new ResultResponse();

        response.setErrorMessages(this.getIndexCardNotFoundError(editIndexCardRequest.getIndexCardId()));

        if (!response.getErrorMessages().isEmpty()) {
            response.setSuccess(false);
            return response;
        }

        IndexCard indexCard = this.indexCardRepo.findIndexCardByIndexcardId(editIndexCardRequest.getIndexCardId());
        indexCard.setQuestion(StandardCharsets.UTF_8.encode(editIndexCardRequest.getQuestion()).array());
        indexCard.setAnswer(StandardCharsets.UTF_8.encode(editIndexCardRequest.getAnswer()).array());
        this.indexCardRepo.save(indexCard);

        response.setSuccess(true);

        return response;
    }
}
