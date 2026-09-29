package com.x7ubi.indexcards.admin;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.models.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminSecurityTest extends AdminTestConfig {

    private static final String ANALYTICS_URL = "/api/admin/analytics";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtUtils jwtUtils;

    private MockMvc mockMvc;

    @BeforeEach
    void setupMockMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).apply(springSecurity()).build();
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
        return "Bearer " + this.jwtUtils.generateJwtTokenForUsername(username);
    }
}
