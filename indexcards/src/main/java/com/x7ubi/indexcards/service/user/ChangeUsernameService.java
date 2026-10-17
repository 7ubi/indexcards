package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UsernameExistsException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.user.ChangeUsernameRequest;
import com.x7ubi.indexcards.response.user.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ChangeUsernameService extends AbstractUserService {

    private final Logger logger = LoggerFactory.getLogger(ChangeUsernameService.class);

    public ChangeUsernameService(UserRepo userRepo) {
        super(userRepo);
    }

    /**
     * Users are identified by their Authentik subject, not their username, so the access token stays valid.
     */
    @Transactional
    public UserResponse changeUsername(String username, ChangeUsernameRequest changeUsernameRequest)
            throws EntityNotFoundException, EntityCreationException, UsernameExistsException {
        User user = getUser(username);

        String newUsername = changeUsernameRequest.getUsername() == null
                ? "" : changeUsernameRequest.getUsername().trim();
        getUsernameError(user, newUsername);

        user.setUsername(newUsername);
        userRepo.save(user);

        logger.info("Username was changed");

        return toUserResponse(user);
    }

    private void getUsernameError(User user, String newUsername)
            throws EntityCreationException, UsernameExistsException {
        if (newUsername.isEmpty()) {
            logger.error(ErrorMessage.User.USERNAME_EMPTY);
            throw new EntityCreationException(ErrorMessage.User.USERNAME_EMPTY);
        }

        if (newUsername.length() > 100) {
            logger.error(ErrorMessage.User.USERNAME_TOO_LONG);
            throw new EntityCreationException(ErrorMessage.User.USERNAME_TOO_LONG);
        }

        if (newUsername.equals(user.getUsername())) {
            logger.error(ErrorMessage.User.USERNAME_UNCHANGED);
            throw new EntityCreationException(ErrorMessage.User.USERNAME_UNCHANGED);
        }

        if (userRepo.existsByUsername(newUsername)) {
            logger.error(ErrorMessage.Authentication.USERNAME_EXITS);
            throw new UsernameExistsException(ErrorMessage.Authentication.USERNAME_EXITS);
        }
    }
}
