package com.x7ubi.indexcards.service.admin;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

/**
 * Gives an existing user the ADMIN role. Granting it to an admin again changes nothing.
 */
@Service
public class GrantAdminService {

    private final Logger logger = LoggerFactory.getLogger(GrantAdminService.class);

    private final UserRepo userRepo;

    public GrantAdminService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public void grantAdmin(String username) throws EntityNotFoundException {
        User user = userRepo.findByUsername(username).orElseThrow(() -> {
            logger.error(ErrorMessage.Project.USERNAME_NOT_FOUND);
            return new EntityNotFoundException(ErrorMessage.Project.USERNAME_NOT_FOUND);
        });

        user.setRole(Role.ADMIN);
        userRepo.save(user);

        logger.info("Admin role was granted");
    }
}
