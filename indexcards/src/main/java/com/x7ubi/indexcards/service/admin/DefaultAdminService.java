package com.x7ubi.indexcards.service.admin;

import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

/**
 * Makes the configured default admins (app.admin.default-usernames, comma separated) admins on every start. Usernames
 * that are not in the database are skipped; they are not reserved, so whoever signs up with such a name becomes admin
 * on the next start.
 */
@Service
public class DefaultAdminService {

    private final Logger logger = LoggerFactory.getLogger(DefaultAdminService.class);

    private final UserRepo userRepo;

    private final List<String> defaultAdmins;

    public DefaultAdminService(UserRepo userRepo, @Value("${app.admin.default-usernames:}") String defaultAdmins) {
        this.userRepo = userRepo;
        this.defaultAdmins = Arrays.stream(defaultAdmins.split(","))
                .map(String::trim)
                .filter(username -> !username.isEmpty())
                .toList();
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void grantDefaultAdmins() {
        for (String username : defaultAdmins) {
            userRepo.findByUsername(username).ifPresentOrElse(user -> {
                if (user.getRole() != Role.ADMIN) {
                    user.setRole(Role.ADMIN);
                    userRepo.save(user);
                    logger.info("Default admin {} was granted the admin role", username);
                }
            }, () -> logger.warn("Default admin {} does not exist, skipping", username));
        }
    }
}
