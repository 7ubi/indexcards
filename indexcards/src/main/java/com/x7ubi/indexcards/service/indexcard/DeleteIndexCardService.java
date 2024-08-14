package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.Project;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.DeleteIndexCardRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class DeleteIndexCardService extends AbstractIndexCardService {
    public DeleteIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper);
    }

    @Transactional
    public void deleteIndexCard(DeleteIndexCardRequest deleteIndexCardRequest) throws EntityNotFoundException {
        getProjectNotFoundError(deleteIndexCardRequest.getProjectId());
        getIndexCardNotFoundError(deleteIndexCardRequest.getIndexcardId());

        IndexCard indexCard = indexCardRepo.findIndexCardByIndexcardId(deleteIndexCardRequest.getIndexcardId());
        Project project = projectRepo.findProjectByProjectId(deleteIndexCardRequest.getProjectId());
        project.getIndexCards().remove(indexCard);
        projectRepo.save(project);
        indexCardRepo.deleteIndexCardByIndexcardId(deleteIndexCardRequest.getIndexcardId());
    }
}
