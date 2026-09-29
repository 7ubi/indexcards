package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class DeleteApiKeyService extends AbstractUserService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteApiKeyService.class);

    public DeleteApiKeyService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        super(userRepo, passwordEncoder);
    }

    @Transactional
    public void deleteApiKey(String username) throws EntityNotFoundException {
        User user = getUser(username);

        user.setAiApiKeyEncrypted(null);
        user.setAiApiKeyHint(null);
        userRepo.save(user);

        logger.info("Gemini API key was deleted");
    }
}
