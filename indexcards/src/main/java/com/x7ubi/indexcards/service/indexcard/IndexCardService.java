package com.x7ubi.indexcards.service.indexcard;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.mapper.IndexCardMapper;
import com.x7ubi.indexcards.models.IndexCard;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.IndexCardAssessmentRepo;
import com.x7ubi.indexcards.repository.IndexCardRepo;
import com.x7ubi.indexcards.repository.ProjectRepo;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.indexcard.IndexCardResponse;
import org.springframework.stereotype.Service;

@Service
public class IndexCardService extends AbstractIndexCardService {
    public IndexCardService(ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper, UserRepo userRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper, userRepo);
    }

    public IndexCardResponse getIndexCard(String username, Long id) throws EntityNotFoundException, UnauthorizedException {
        this.getIndexCardNotFoundError(id);

        User user = this.getUser(username);

        IndexCard indexCard = this.indexCardRepo.findIndexCardByIndexcardId(id);

        getProjectOwnerError(user, indexCard.getProject());

        return this.indexCardMapper.mapToResponse(indexCard);
    }
}
