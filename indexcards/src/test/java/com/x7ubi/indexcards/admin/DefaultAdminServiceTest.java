package com.x7ubi.indexcards.admin;

import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.service.admin.DefaultAdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DefaultAdminServiceTest extends AdminTestConfig {

    @Test
    public void grantDefaultAdminsTest() {
        // given
        DefaultAdminService defaultAdminService = new DefaultAdminService(this.userRepo, " test , unknown,,admin");

        // when
        defaultAdminService.grantDefaultAdmins();

        // then
        Assertions.assertEquals(Role.ADMIN, this.userRepo.findByUsername("test").orElseThrow().getRole());
        Assertions.assertEquals(Role.ADMIN, this.userRepo.findByUsername("admin").orElseThrow().getRole());
        Assertions.assertTrue(this.userRepo.findByUsername("unknown").isEmpty());
    }

    @Test
    public void noDefaultAdminsTest() {
        // given
        DefaultAdminService defaultAdminService = new DefaultAdminService(this.userRepo, "");

        // when
        defaultAdminService.grantDefaultAdmins();

        // then
        Assertions.assertEquals(Role.USER, this.userRepo.findByUsername("test").orElseThrow().getRole());
    }
}
