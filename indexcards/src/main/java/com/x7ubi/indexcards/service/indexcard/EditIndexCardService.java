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
import com.x7ubi.indexcards.request.indexcard.CreateIndexCardRequest;
import org.springframework.stereotype.Service;

@Service
public class EditIndexCardService extends AbstractIndexCardService {

    public EditIndexCardService(ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper, UserRepo userRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper, userRepo);
    }

    public void editIndexCard(String username, Long id, CreateIndexCardRequest request) throws EntityNotFoundException, UnauthorizedException {
        this.getIndexCardNotFoundError(id);

        User user = this.getUser(username);
        IndexCard indexCard = this.indexCardRepo.findIndexCardByIndexcardId(id);

        getProjectOwnerError(user, indexCard.getProject());

        this.indexCardMapper.mapRequestToIndexCard(request, indexCard);
        this.indexCardRepo.save(indexCard);
    }
}
