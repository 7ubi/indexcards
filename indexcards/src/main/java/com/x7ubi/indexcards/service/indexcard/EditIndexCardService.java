package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import org.springframework.stereotype.Service;

@Service
public class EditIndexCardService extends AbstractIndexCardService {

    public EditIndexCardService(ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper);
    }

    public void editIndexCard(Long id, CreateIndexCardRequest request) throws EntityNotFoundException {
        this.getIndexCardNotFoundError(id);

        IndexCard indexCard = this.indexCardRepo.findIndexCardByIndexcardId(id);
        this.indexCardMapper.mapRequestToIndexCard(request, indexCard);
        this.indexCardRepo.save(indexCard);
    }
}
