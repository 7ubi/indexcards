package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.AuthentikDeletionException;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.exceptions.UsernameExistsException;
import com.x7ubi.indexcards.models.SecurityUser;
import com.x7ubi.indexcards.request.user.ChangeUsernameRequest;
import com.x7ubi.indexcards.request.user.DeleteAccountRequest;
import com.x7ubi.indexcards.response.user.UserResponse;
import com.x7ubi.indexcards.service.user.ChangeUsernameService;
import com.x7ubi.indexcards.service.user.DeleteUserService;
import com.x7ubi.indexcards.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserRestController {
    Logger logger = LoggerFactory.getLogger(UserRestController.class);

    private final UserService userService;

    private final ChangeUsernameService changeUsernameService;

    private final DeleteUserService deleteUserService;

    public UserRestController(
            UserService userService, ChangeUsernameService changeUsernameService,
            DeleteUserService deleteUserService) {
        this.userService = userService;
        this.changeUsernameService = changeUsernameService;
        this.deleteUserService = deleteUserService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUser(
            @AuthenticationPrincipal SecurityUser securityUser
    ) throws EntityNotFoundException {
        logger.info("Getting user account");
        String username = securityUser.getUsername();

        return ResponseEntity.ok(userService.getUserResponse(username));
    }

    @PutMapping("/username")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> changeUsername(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody ChangeUsernameRequest changeUsernameRequest
    ) throws EntityNotFoundException, EntityCreationException, UsernameExistsException {
        logger.info("Changing username");
        String username = securityUser.getUsername();

        return ResponseEntity.ok(changeUsernameService.changeUsername(username, changeUsernameRequest));
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteUser(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody DeleteAccountRequest deleteAccountRequest
    ) throws EntityNotFoundException, UnauthorizedException, AuthentikDeletionException {
        logger.info("Deleting user account");
        String username = securityUser.getUsername();

        deleteUserService.deleteUser(username, deleteAccountRequest);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
