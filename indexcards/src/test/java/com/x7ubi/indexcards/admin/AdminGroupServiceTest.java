package com.x7ubi.indexcards.admin;

import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.service.admin.AdminGroupService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public class AdminGroupServiceTest extends AdminTestConfig {

    @Autowired
    private AdminGroupService adminGroupService;

    @Test
    public void groupMemberIsGrantedAdminTest() {
        // when
        User result = this.adminGroupService.grantAdminFromGroups(this.user, jwt(List.of("other", "indexcards-admins")));

        // then
        Assertions.assertEquals(Role.ADMIN, result.getRole());
        Assertions.assertEquals(Role.ADMIN, this.userRepo.findByUsername("test").orElseThrow().getRole());
    }

    @Test
    public void otherGroupsStayUserTest() {
        // when
        this.adminGroupService.grantAdminFromGroups(this.user, jwt(List.of("other")));

        // then
        Assertions.assertEquals(Role.USER, this.userRepo.findByUsername("test").orElseThrow().getRole());
    }

    @Test
    public void missingGroupsClaimStaysUserTest() {
        // when
        this.adminGroupService.grantAdminFromGroups(this.user, jwt(null));

        // then
        Assertions.assertEquals(Role.USER, this.userRepo.findByUsername("test").orElseThrow().getRole());
    }

    @Test
    public void adminOutsideGroupKeepsRoleTest() {
        // when (promoted from the analytics page, not a group member)
        this.adminGroupService.grantAdminFromGroups(this.admin, jwt(List.of()));

        // then
        Assertions.assertEquals(Role.ADMIN, this.userRepo.findByUsername("admin").orElseThrow().getRole());
    }

    private static Jwt jwt(List<String> groups) {
        Jwt.Builder builder = Jwt.withTokenValue("token").header("alg", "RS256").subject("subject");
        if (groups != null) {
            builder.claim("groups", groups);
        }
        return builder.build();
    }
}
