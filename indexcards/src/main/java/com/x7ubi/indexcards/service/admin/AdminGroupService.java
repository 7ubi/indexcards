package com.x7ubi.indexcards.service.admin;

import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Makes members of the Authentik group app.admin.group (the access token's "groups" claim) admins. Only Authentik
 * admins can change group memberships, so users cannot grant themselves the role. The role is only granted, never
 * revoked: admins promoted from the analytics page don't have to be group members.
 */
@Service
public class AdminGroupService {

    private static final String GROUPS_CLAIM = "groups";

    private final Logger logger = LoggerFactory.getLogger(AdminGroupService.class);

    private final UserRepo userRepo;

    private final String adminGroup;

    public AdminGroupService(UserRepo userRepo, @Value("${app.admin.group:indexcards-admins}") String adminGroup) {
        this.userRepo = userRepo;
        this.adminGroup = adminGroup;
    }

    public User grantAdminFromGroups(User user, Jwt jwt) {
        List<String> groups = jwt.getClaimAsStringList(GROUPS_CLAIM);
        if (user.getRole() == Role.ADMIN || groups == null || !groups.contains(adminGroup)) {
            return user;
        }

        user.setRole(Role.ADMIN);
        logger.info("Member of Authentik group {} was granted the admin role", adminGroup);
        return userRepo.save(user);
    }
}
