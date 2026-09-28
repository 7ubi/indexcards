package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.exceptions.UsernameExistsException;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.request.user.ChangeUsernameRequest;
import com.x7ubi.indexcards.response.common.JwtResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
public class ChangeUsernameService extends AbstractUserService {

    private final Logger logger = LoggerFactory.getLogger(ChangeUsernameService.class);

    private final JwtUtils jwtUtils;

    public ChangeUsernameService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        super(userRepo, passwordEncoder);
        this.jwtUtils = jwtUtils;
    }

    /**
     * The username is the subject of the JWT, so the caller receives a new token for the new username.
     */
    @Transactional
    public JwtResponse changeUsername(String username, ChangeUsernameRequest changeUsernameRequest)
            throws EntityNotFoundException, UnauthorizedException, EntityCreationException, UsernameExistsException {
        User user = getUser(username);
        getWrongPasswordError(user, changeUsernameRequest.getPassword());

        String newUsername = changeUsernameRequest.getUsername() == null
                ? "" : changeUsernameRequest.getUsername().trim();
        getUsernameError(user, newUsername);

        user.setUsername(newUsername);
        userRepo.save(user);

        logger.info("Username was changed");

        return new JwtResponse(jwtUtils.generateJwtTokenForUsername(newUsername), user.getId(), newUsername);
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
