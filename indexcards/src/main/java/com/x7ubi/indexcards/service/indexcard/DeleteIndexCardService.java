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
import com.x7ubi.indexcards.request.indexcard.DeleteIndexCardRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class DeleteIndexCardService extends AbstractIndexCardService {
    public DeleteIndexCardService(
            ProjectRepo projectRepo, IndexCardRepo indexCardRepo, IndexCardAssessmentRepo indexCardAssessmentRepo, IndexCardMapper indexCardMapper, UserRepo userRepo) {
        super(projectRepo, indexCardRepo, indexCardAssessmentRepo, indexCardMapper, userRepo);
    }

    @Transactional
    public void deleteIndexCard(String username, DeleteIndexCardRequest deleteIndexCardRequest) throws EntityNotFoundException, UnauthorizedException {
        getIndexCardNotFoundError(deleteIndexCardRequest.getIndexcardId());

        IndexCard indexCard = indexCardRepo.findIndexCardByIndexcardId(deleteIndexCardRequest.getIndexcardId());
        User user = getUser(username);

        getProjectOwnerError(user, indexCard.getProject());

        this.indexCardRepo.delete(indexCard);
    }
}
