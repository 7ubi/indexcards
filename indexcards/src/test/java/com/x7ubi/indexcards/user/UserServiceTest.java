package com.x7ubi.indexcards.user;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.models.Role;
import com.x7ubi.indexcards.response.user.UserResponse;
import com.x7ubi.indexcards.service.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserServiceTest extends UserTestConfig {

    @Autowired
    private UserService userService;

    @Test
    public void getUserTest() throws EntityNotFoundException {
        // when
        UserResponse userResponse = this.userService.getUserResponse(this.user.getUsername());

        // then
        Assertions.assertEquals("test", userResponse.getUsername());
        Assertions.assertEquals("Max", userResponse.getFirstname());
        Assertions.assertEquals("Muster", userResponse.getSurname());
        Assertions.assertFalse(userResponse.isAdmin());
    }

    @Test
    public void getAdminUserTest() throws EntityNotFoundException {
        // given
        this.user.setRole(Role.ADMIN);
        this.userRepo.save(this.user);

        // when
        UserResponse userResponse = this.userService.getUserResponse(this.user.getUsername());

        // then
        Assertions.assertTrue(userResponse.isAdmin());
    }

    @Test
    public void getUnknownUserTest() {
        // when
        EntityNotFoundException entityNotFoundException = Assertions.assertThrows(EntityNotFoundException.class, () ->
                this.userService.getUserResponse("unknown"));

        // then
        Assertions.assertEquals(ErrorMessage.Project.USERNAME_NOT_FOUND, entityNotFoundException.getMessage());
    }
}
