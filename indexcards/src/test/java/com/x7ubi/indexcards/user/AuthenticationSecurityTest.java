package com.x7ubi.indexcards.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.x7ubi.indexcards.service.authentication.OidcUserProvisioningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Duration;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// The @MockitoBean makes this a separate Spring context; give it its own in-memory database so closing one context
// (see @DirtiesContext in the base class) cannot close the database of another cached context.
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:authsecuritytestdb;NON_KEYWORDS=USER")
public class AuthenticationSecurityTest extends UserTestConfig {

    private static final String KNOWN_SUBJECT = "subject-test";

    @Autowired
    private WebApplicationContext context;

    // Replaces the Authentik-backed decoder; in these tests the token value is the token's subject.
    @MockitoBean
    private JwtDecoder jwtDecoder;

    @MockitoSpyBean
    private OidcUserProvisioningService oidcUserProvisioningService;

    private MockMvc mockMvc;

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).apply(springSecurity()).build();
        when(this.jwtDecoder.decode(anyString())).thenAnswer(invocation -> jwt(invocation.getArgument(0)));
        doThrow(new BadJwtException("invalid")).when(this.jwtDecoder).decode("invalid");

        this.user.setOidcSubject(KNOWN_SUBJECT);
        this.userRepo.save(this.user);
    }

    @Test
    public void knownSubjectGetsOwnAccountTest() throws Exception {
        this.mockMvc.perform(get("/api/user").header("Authorization", "Bearer " + KNOWN_SUBJECT))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test"));

        Assertions.assertEquals(2, this.userRepo.count());
    }

    @Test
    public void firstLoginCreatesAccountTest() throws Exception {
        this.mockMvc.perform(get("/api/user").header("Authorization", "Bearer new-subject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.firstname").value("Alice"))
                .andExpect(jsonPath("$.surname").value("Liddell"));

        Assertions.assertEquals(3, this.userRepo.count());
    }

    @Test
    public void invalidTokenIsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(get("/api/user").header("Authorization", "Bearer invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void staleTokenOfUnknownSubjectIsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(get("/api/user").header("Authorization", "Bearer stale-subject"))
                .andExpect(status().isUnauthorized());

        Assertions.assertEquals(2, this.userRepo.count());
    }

    @Test
    public void databaseFailureDuringProvisioningIsServiceUnavailableTest() throws Exception {
        doThrow(new DataAccessResourceFailureException("db down")).when(this.oidcUserProvisioningService)
                .getOrCreateUser(argThat(jwt -> jwt != null && "db-down".equals(jwt.getSubject())));

        this.mockMvc.perform(get("/api/user").header("Authorization", "Bearer db-down"))
                .andExpect(status().isServiceUnavailable());
    }

    @Test
    public void missingTokenIsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(get("/api/user")).andExpect(status().isUnauthorized());
    }

    @Test
    public void authConfigIsPublicTest() throws Exception {
        this.mockMvc.perform(get("/api/auth/config"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authority").value("http://localhost:9000/application/o/indexcards/"))
                .andExpect(jsonPath("$.clientId").value("indexcards"));
    }

    @Test
    public void passwordLoginIsGoneTest() throws Exception {
        this.mockMvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"username\":\"test\",\"password\":\"1234\"}"))
                .andExpect(status().is4xxClientError());
    }

    private static Jwt jwt(String subject) {
        Instant issuedAt = subject.startsWith("stale-") ? Instant.now().minus(Duration.ofHours(1)) : Instant.now();
        return Jwt.withTokenValue(subject)
                .header("alg", "RS256")
                .issuedAt(issuedAt)
                .subject(subject)
                .claim("preferred_username", "alice")
                .claim("name", "Alice Liddell")
                .build();
    }
}
