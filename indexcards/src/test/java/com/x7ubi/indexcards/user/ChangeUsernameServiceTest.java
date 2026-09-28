package com.x7ubi.indexcards.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.exceptions.UsernameExistsException;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.request.user.ChangeUsernameRequest;
import com.x7ubi.indexcards.response.common.JwtResponse;
import com.x7ubi.indexcards.service.user.ChangeUsernameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ChangeUsernameServiceTest extends UserTestConfig {

    @Autowired
    private ChangeUsernameService changeUsernameService;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    public void changeUsernameTest()
            throws EntityNotFoundException, UnauthorizedException, EntityCreationException, UsernameExistsException {
        // when
        JwtResponse jwtResponse = this.changeUsernameService.changeUsername(
                this.user.getUsername(), new ChangeUsernameRequest("  renamed  ", PASSWORD));

        // then
        Assertions.assertTrue(this.userRepo.findByUsername("test").isEmpty());
        Assertions.assertTrue(this.userRepo.findByUsername("renamed").isPresent());
        Assertions.assertEquals("renamed", jwtResponse.getUsername());
        Assertions.assertEquals(this.user.getId(), jwtResponse.getId());
        Assertions.assertTrue(this.jwtUtils.validateJwtToken(jwtResponse.getToken()));
        Assertions.assertEquals("renamed", this.jwtUtils.getUsernameFromJwtToken(jwtResponse.getToken()));
    }

    @Test
    public void changeUsernameWithWrongPasswordTest() {
        // when
        UnauthorizedException unauthorizedException = Assertions.assertThrows(UnauthorizedException.class, () ->
                this.changeUsernameService.changeUsername(
                        this.user.getUsername(), new ChangeUsernameRequest("renamed", "wrong")));

        // then
        Assertions.assertEquals(ErrorMessage.User.WRONG_PASSWORD, unauthorizedException.getMessage());
        assertUsernameUnchanged();
    }

    @Test
    public void changeUsernameToExistingUsernameTest() {
        // when
        UsernameExistsException usernameExistsException = Assertions.assertThrows(UsernameExistsException.class, () ->
                this.changeUsernameService.changeUsername(
                        this.user.getUsername(), new ChangeUsernameRequest(this.user2.getUsername(), PASSWORD)));

        // then
        Assertions.assertEquals(ErrorMessage.Authentication.USERNAME_EXITS, usernameExistsException.getMessage());
        assertUsernameUnchanged();
    }

    @Test
    public void changeUsernameToBlankUsernameTest() {
        assertEntityCreationError("   ", ErrorMessage.User.USERNAME_EMPTY);
        assertEntityCreationError(null, ErrorMessage.User.USERNAME_EMPTY);
    }

    @Test
    public void changeUsernameToTooLongUsernameTest() {
        assertEntityCreationError("a".repeat(101), ErrorMessage.User.USERNAME_TOO_LONG);
    }

    @Test
    public void changeUsernameToSameUsernameTest() {
        assertEntityCreationError(this.user.getUsername(), ErrorMessage.User.USERNAME_UNCHANGED);
    }

    private void assertEntityCreationError(String newUsername, String error) {
        // when
        EntityCreationException entityCreationException = Assertions.assertThrows(EntityCreationException.class, () ->
                this.changeUsernameService.changeUsername(
                        this.user.getUsername(), new ChangeUsernameRequest(newUsername, PASSWORD)));

        // then
        Assertions.assertEquals(error, entityCreationException.getMessage());
        assertUsernameUnchanged();
    }

    private void assertUsernameUnchanged() {
        Assertions.assertTrue(this.userRepo.findByUsername(this.user.getUsername()).isPresent());
        Assertions.assertTrue(this.userRepo.findByUsername(this.user2.getUsername()).isPresent());
        Assertions.assertEquals(2, this.userRepo.count());
    }
}
