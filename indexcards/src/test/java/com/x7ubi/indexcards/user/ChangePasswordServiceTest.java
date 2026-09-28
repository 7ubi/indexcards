package com.x7ubi.indexcards.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.request.user.ChangePasswordRequest;
import com.x7ubi.indexcards.service.user.ChangePasswordService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChangePasswordServiceTest extends UserTestConfig {

    @Autowired
    private ChangePasswordService changePasswordService;

    @Test
    public void changePasswordTest() throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        // when
        this.changePasswordService.changePassword(
                this.user.getUsername(), new ChangePasswordRequest(PASSWORD, "newPassword"));

        // then
        User changedUser = this.userRepo.findByUsername(this.user.getUsername()).orElseThrow();
        Assertions.assertTrue(this.passwordEncoder.matches("newPassword", changedUser.getPassword()));
        Assertions.assertFalse(this.passwordEncoder.matches(PASSWORD, changedUser.getPassword()));
    }

    @Test
    public void changePasswordWithWrongCurrentPasswordTest() {
        // when
        UnauthorizedException unauthorizedException = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.changePasswordService.changePassword(
                        this.user.getUsername(), new ChangePasswordRequest("wrong", "newPassword")));

        // then
        Assertions.assertEquals(ErrorMessage.User.WRONG_PASSWORD, unauthorizedException.getMessage());
        assertPasswordUnchanged();
    }

    @Test
    public void changePasswordToBlankPasswordTest() {
        // when
        EntityCreationException entityCreationException = Assertions.assertThrows(EntityCreationException.class, () ->
                this.changePasswordService.changePassword(
                        this.user.getUsername(), new ChangePasswordRequest(PASSWORD, " ")));

        // then
        Assertions.assertEquals(ErrorMessage.User.PASSWORD_EMPTY, entityCreationException.getMessage());
        assertPasswordUnchanged();
    }

    private void assertPasswordUnchanged() {
        User unchangedUser = this.userRepo.findByUsername(this.user.getUsername()).orElseThrow();
        Assertions.assertTrue(this.passwordEncoder.matches(PASSWORD, unchangedUser.getPassword()));
    }
}
