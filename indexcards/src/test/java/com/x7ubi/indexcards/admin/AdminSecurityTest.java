package com.x7ubi.indexcards.admin;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// The @MockitoBean makes this a separate Spring context; give it its own in-memory database so closing one context
// (see @DirtiesContext in the base class) cannot close the database of another cached context.
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:adminsecuritytestdb;NON_KEYWORDS=USER")
public class AdminSecurityTest extends AdminTestConfig {

    private static final String ANALYTICS_URL = "/api/admin/analytics";

    @Autowired
    private WebApplicationContext context;

    // Replaces the Authentik-backed decoder; in these tests the token value is the token's subject.
    @MockitoBean
    private JwtDecoder jwtDecoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).apply(springSecurity()).build();
        when(this.jwtDecoder.decode(anyString())).thenAnswer(invocation -> Jwt.withTokenValue(invocation.getArgument(0))
                .header("alg", "RS256")
                .subject(invocation.getArgument(0))
                .build());
    }

    @Test
    public void analyticsWithoutTokenIsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(get(ANALYTICS_URL)).andExpect(status().isUnauthorized());
    }

    @Test
    public void analyticsAsUserIsForbiddenTest() throws Exception {
        this.mockMvc.perform(get(ANALYTICS_URL).header("Authorization", bearer(this.user.getUsername())))
                .andExpect(status().isForbidden())
                .andExpect(content().string(ErrorMessage.Authentication.FORBIDDEN));
    }

    @Test
    public void analyticsAsAdminIsOkTest() throws Exception {
        this.mockMvc.perform(get(ANALYTICS_URL).header("Authorization", bearer(this.admin.getUsername())))
                .andExpect(status().isOk());
    }

    @Test
    public void grantAdminAsUserIsForbiddenTest() throws Exception {
        this.mockMvc.perform(put(grantAdminUrl(this.user.getUsername()))
                        .header("Authorization", bearer(this.user.getUsername())))
                .andExpect(status().isForbidden());

        Assertions.assertEquals(Role.USER, this.userRepo.findByUsername("test").orElseThrow().getRole());
    }

    @Test
    public void grantAdminAsAdminTest() throws Exception {
        this.mockMvc.perform(put(grantAdminUrl(this.user.getUsername()))
                        .header("Authorization", bearer(this.admin.getUsername())))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(Role.ADMIN, this.userRepo.findByUsername("test").orElseThrow().getRole());
    }

    private static String grantAdminUrl(String username) {
        return "/api/admin/users/" + username + "/admin";
    }

    private String bearer(String username) {
        User user = this.userRepo.findByUsername(username).orElseThrow();
        user.setOidcSubject("subject-" + username);
        this.userRepo.save(user);
        return "Bearer " + user.getOidcSubject();
    }
}
