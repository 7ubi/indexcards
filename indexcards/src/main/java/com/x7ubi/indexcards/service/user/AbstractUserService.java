package com.x7ubi.indexcards.service.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import com.x7ubi.indexcards.response.user.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractUserService {
    private final Logger logger = LoggerFactory.getLogger(AbstractUserService.class);

    protected final UserRepo userRepo;

    public AbstractUserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    protected User getUser(String username) throws EntityNotFoundException {
        return userRepo.findByUsername(username).orElseThrow(() -> {
            logger.error(ErrorMessage.Project.USERNAME_NOT_FOUND);
            return new EntityNotFoundException(ErrorMessage.Project.USERNAME_NOT_FOUND);
        });
    }

    protected static UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getUsername(), user.getFirstname(), user.getSurname(), user.getRole() == Role.ADMIN);
    }
}
