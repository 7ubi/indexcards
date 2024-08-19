package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import org.springframework.stereotype.Service;

@Service
public class IndexCardService extends AbstractIndexCardService {
    public IndexCardService(ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper);
    }

    public IndexCardResponse getIndexCard(Long id) throws EntityNotFoundException {
        this.getIndexCardNotFoundError(id);

        IndexCard indexCard = this.indexCardRepo.findIndexCardByIndexcardId(id);

        return this.indexCardMapper.mapToResponse(indexCard);
    }
}
