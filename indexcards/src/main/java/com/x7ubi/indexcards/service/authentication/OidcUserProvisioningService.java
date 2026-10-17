package com.x7ubi.indexcards.service.authentication;

import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.repository.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Maps an Authentik access token to the local {@link User}. The token's subject identifies the account; the account is
 * created on the first request after the user's first login.
 */
@Service
public class OidcUserProvisioningService {

    private static final int MAX_NAME_LENGTH = 100;

    private static final String FALLBACK_USERNAME = "user";

    private static final Duration MAX_PROVISIONING_TOKEN_AGE = Duration.ofMinutes(5);

    private final Logger logger = LoggerFactory.getLogger(OidcUserProvisioningService.class);

    private final UserRepo userRepo;

    private final Clock clock;

    public OidcUserProvisioningService(UserRepo userRepo, Clock clock) {
        this.userRepo = userRepo;
        this.clock = clock;
    }

    public User getOrCreateUser(Jwt jwt) {
        String subject = jwt.getSubject();
        if (!StringUtils.hasText(subject)) {
            throw new InvalidBearerTokenException("Token has no subject");
        }

        return userRepo.findByOidcSubject(subject).orElseGet(() -> createUser(subject, jwt));
    }

    private User createUser(String subject, Jwt jwt) {
        // An access token stays valid for up to 24 h, also after the account (and its Authentik user) was deleted.
        // Without this check another tab with the old token would re-create the deleted account. A fresh token
        // (issued by a new login) proves the Authentik user still exists.
        Instant issuedAt = jwt.getIssuedAt();
        if (issuedAt == null || issuedAt.isBefore(clock.instant().minus(MAX_PROVISIONING_TOKEN_AGE))) {
            throw new InvalidBearerTokenException("Token is too old to create an account, please log in again");
        }

        User user = new User();
        user.setOidcSubject(subject);
        user.setUsername(getUniqueUsername(jwt.getClaimAsString("preferred_username")));
        setNames(user, jwt.getClaimAsString("name"));
        user.setProjects(new ArrayList<>());
        user.setCreatedAt(LocalDateTime.now());

        try {
            User savedUser = userRepo.saveAndFlush(user);
            logger.info("User was created on first login");
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            // A parallel first request of the same user created the account in the meantime.
            return userRepo.findByOidcSubject(subject).orElseThrow(() -> e);
        }
    }

    /**
     * Usernames are unique in the app but not necessarily in Authentik (and legacy accounts still hold theirs), so a
     * taken username gets a numeric suffix.
     */
    private String getUniqueUsername(String preferredUsername) {
        String base = StringUtils.hasText(preferredUsername) ? preferredUsername.trim() : FALLBACK_USERNAME;
        String username = truncate(base, MAX_NAME_LENGTH);
        for (int i = 2; userRepo.existsByUsername(username); i++) {
            String suffix = "-" + i;
            username = truncate(base, MAX_NAME_LENGTH - suffix.length()) + suffix;
        }
        return username;
    }

    /**
     * Authentik's profile scope only contains the full name, so its last word is used as the surname.
     */
    private static void setNames(User user, String name) {
        String fullName = name == null ? "" : name.trim();
        int lastSpace = fullName.lastIndexOf(' ');
        if (lastSpace < 0) {
            user.setFirstname(truncate(fullName, MAX_NAME_LENGTH));
            user.setSurname("");
            return;
        }

        user.setFirstname(truncate(fullName.substring(0, lastSpace).trim(), MAX_NAME_LENGTH));
        user.setSurname(truncate(fullName.substring(lastSpace + 1), MAX_NAME_LENGTH));
    }

    private static String truncate(String value, int maxLength) {
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
