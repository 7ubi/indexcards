package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AbstractUserService {
    private final Logger logger = LoggerFactory.getLogger(AbstractUserService.class);

    protected final UserRepo userRepo;

    protected final PasswordEncoder passwordEncoder;

    public AbstractUserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    protected User getUser(String username) throws EntityNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> {
            logger.error(ErrorMessage.Project.USERNAME_NOT_FOUND);
            return new EntityNotFoundException(ErrorMessage.Project.USERNAME_NOT_FOUND);
        });
    }

    /**
     * Sensitive account operations require the current password again, even with a valid token.
     */
    protected void getWrongPasswordError(User user, String password) throws UnauthorizedException {
        if (password == null || !passwordEncoder.matches(password, user.getPassword())) {
            logger.error(ErrorMessage.User.WRONG_PASSWORD);
            throw new UnauthorizedException(ErrorMessage.User.WRONG_PASSWORD);
        }
    }
}
