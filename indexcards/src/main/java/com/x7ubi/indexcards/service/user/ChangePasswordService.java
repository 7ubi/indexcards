package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.user.ChangePasswordRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ChangePasswordService extends AbstractUserService {

    private final Logger logger = LoggerFactory.getLogger(ChangePasswordService.class);

    public ChangePasswordService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        super(userRepo, passwordEncoder);
    }

    @Transactional
    public void changePassword(String username, ChangePasswordRequest changePasswordRequest)
            throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        User user = getUser(username);
        getWrongPasswordError(user, changePasswordRequest.getCurrentPassword());

        String newPassword = changePasswordRequest.getNewPassword();
        if (newPassword == null || newPassword.isBlank()) {
            logger.error(ErrorMessage.User.PASSWORD_EMPTY);
            throw new EntityCreationException(ErrorMessage.User.PASSWORD_EMPTY);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        logger.info("Password was changed");
    }
}
