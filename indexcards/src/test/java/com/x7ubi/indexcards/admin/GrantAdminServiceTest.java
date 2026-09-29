package com.x7ubi.indexcards.admin;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.service.admin.GrantAdminService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class GrantAdminServiceTest extends AdminTestConfig {

    @Autowired
    private GrantAdminService grantAdminService;

    @Test
    public void grantAdminTest() throws EntityNotFoundException {
        // when
        this.grantAdminService.grantAdmin(this.user.getUsername());

        // then
        Assertions.assertEquals(Role.ADMIN, this.userRepo.findByUsername("test").orElseThrow().getRole());
        Assertions.assertEquals(Role.ADMIN, this.userRepo.findByUsername("admin").orElseThrow().getRole());
    }

    @Test
    public void grantAdminToAdminTest() throws EntityNotFoundException {
        // when
        this.grantAdminService.grantAdmin(this.admin.getUsername());

        // then
        Assertions.assertEquals(Role.ADMIN, this.userRepo.findByUsername("admin").orElseThrow().getRole());
        Assertions.assertEquals(Role.USER, this.userRepo.findByUsername("test").orElseThrow().getRole());
    }

    @Test
    public void grantAdminToUnknownUserTest() {
        // when
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.grantAdminService.grantAdmin("unknown"));

        // then
        Assertions.assertEquals(ErrorMessage.Project.USERNAME_NOT_FOUND, exception.getMessage());
    }
}
