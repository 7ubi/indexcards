package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.DeleteIndexCardRequest;
import com.x7ubi.indexcards.response.common.ResultResponse;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class DeleteIndexCardService extends AbstractIndexCardService {
    public DeleteIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo);
    }

    @Transactional
    public ResultResponse deleteIndexCard(DeleteIndexCardRequest deleteIndexCardRequest) {
        ResultResponse response = new ResultResponse();

        response.setErrorMessages(getProjectNotFoundError(deleteIndexCardRequest.getProjectId()));

        if (response.getErrorMessages().isEmpty()) {
            response.setErrorMessages(getIndexCardNotFoundError(deleteIndexCardRequest.getIndexcardId()));
        }

        if (!response.getErrorMessages().isEmpty()) {
            response.setSuccess(false);
            return response;
        }

        IndexCard indexCard = indexCardRepo.findIndexCardByIndexcardId(deleteIndexCardRequest.getIndexcardId());
        Project project = projectRepo.findProjectByProjectId(deleteIndexCardRequest.getProjectId());
        project.getIndexCards().remove(indexCard);
        projectRepo.save(project);
        indexCardRepo.deleteIndexCardByIndexcardId(deleteIndexCardRequest.getIndexcardId());

        response.setSuccess(true);

        return response;
    }
}
