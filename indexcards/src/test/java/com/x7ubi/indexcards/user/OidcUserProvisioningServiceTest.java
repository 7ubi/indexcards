package com.x7ubi.indexcards.user;

import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.service.authentication.OidcUserProvisioningService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

public class OidcUserProvisioningServiceTest extends UserTestConfig {

    @Autowired
    private OidcUserProvisioningService oidcUserProvisioningService;

    @Test
    public void firstLoginCreatesUserTest() {
        // when
        User created = this.oidcUserProvisioningService.getOrCreateUser(jwt("subject-1", "alice", "Alice Pleasance Liddell"));

        // then
        Assertions.assertEquals(3, this.userRepo.count());
        User saved = this.userRepo.findByOidcSubject("subject-1").orElseThrow();
        Assertions.assertEquals(created.getId(), saved.getId());
        Assertions.assertEquals("alice", saved.getUsername());
        Assertions.assertEquals("Alice Pleasance", saved.getFirstname());
        Assertions.assertEquals("Liddell", saved.getSurname());
        Assertions.assertEquals(Role.USER, saved.getRole());
        Assertions.assertNotNull(saved.getCreatedAt());
    }

    @Test
    public void knownSubjectReturnsExistingUserTest() {
        // given
        User first = this.oidcUserProvisioningService.getOrCreateUser(jwt("subject-1", "alice", "Alice Liddell"));
        first.setUsername("renamed");
        this.userRepo.save(first);

        // when
        User second = this.oidcUserProvisioningService.getOrCreateUser(jwt("subject-1", "alice", "Alice Liddell"));

        // then
        Assertions.assertEquals(first.getId(), second.getId());
        Assertions.assertEquals("renamed", second.getUsername());
        Assertions.assertEquals(3, this.userRepo.count());
    }

    @Test
    public void takenUsernameGetsSuffixTest() {
        // when ("test" belongs to the fixture user)
        User created = this.oidcUserProvisioningService.getOrCreateUser(jwt("subject-1", "test", "Max Muster"));

        // then
        Assertions.assertEquals("test-2", created.getUsername());
    }

    @Test
    public void missingClaimsUseFallbacksTest() {
        // when
        User created = this.oidcUserProvisioningService.getOrCreateUser(jwt("subject-1", null, null));

        // then
        Assertions.assertEquals("user", created.getUsername());
        Assertions.assertEquals("", created.getFirstname());
        Assertions.assertEquals("", created.getSurname());
    }

    @Test
    public void singleWordNameIsFirstnameTest() {
        // when
        User created = this.oidcUserProvisioningService.getOrCreateUser(jwt("subject-1", "alice", "Alice"));

        // then
        Assertions.assertEquals("Alice", created.getFirstname());
        Assertions.assertEquals("", created.getSurname());
    }

    @Test
    public void longUsernameIsTruncatedTest() {
        // given
        String longName = "a".repeat(150);
        this.oidcUserProvisioningService.getOrCreateUser(jwt("subject-1", longName, null));

        // when
        User second = this.oidcUserProvisioningService.getOrCreateUser(jwt("subject-2", longName, null));

        // then
        Assertions.assertEquals("a".repeat(100), this.userRepo.findByOidcSubject("subject-1").orElseThrow().getUsername());
        Assertions.assertEquals("a".repeat(98) + "-2", second.getUsername());
    }

    @Test
    public void tokenWithoutSubjectIsRejectedTest() {
        // given
        Jwt jwt = Jwt.withTokenValue("token").header("alg", "RS256").claim("preferred_username", "alice").build();

        // when / then
        Assertions.assertThrows(InvalidBearerTokenException.class,
                () -> this.oidcUserProvisioningService.getOrCreateUser(jwt));
        Assertions.assertEquals(2, this.userRepo.count());
    }

    private static Jwt jwt(String subject, String preferredUsername, String name) {
        Jwt.Builder builder = Jwt.withTokenValue("token").header("alg", "RS256").subject(subject);
        if (preferredUsername != null) {
            builder.claim("preferred_username", preferredUsername);
        }
        if (name != null) {
            builder.claim("name", name);
        }
        return builder.build();
    }
}
