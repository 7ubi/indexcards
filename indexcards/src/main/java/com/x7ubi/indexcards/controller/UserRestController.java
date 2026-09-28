package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.exceptions.UsernameExistsException;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.request.user.ChangePasswordRequest;
import com.x7ubi.indexcards.request.user.ChangeUsernameRequest;
import com.x7ubi.indexcards.request.user.DeleteAccountRequest;
import com.x7ubi.indexcards.response.common.JwtResponse;
import com.x7ubi.indexcards.response.user.UserResponse;
import com.x7ubi.indexcards.service.user.ChangePasswordService;
import com.x7ubi.indexcards.service.user.ChangeUsernameService;
import com.x7ubi.indexcards.service.user.DeleteUserService;
import com.x7ubi.indexcards.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserRestController {
    Logger logger = LoggerFactory.getLogger(UserRestController.class);

    private final JwtUtils jwtUtils;

    private final UserService userService;

    private final ChangeUsernameService changeUsernameService;

    private final ChangePasswordService changePasswordService;

    private final DeleteUserService deleteUserService;

    public UserRestController(
            JwtUtils jwtUtils, UserService userService, ChangeUsernameService changeUsernameService,
            ChangePasswordService changePasswordService, DeleteUserService deleteUserService) {
        this.jwtUtils = jwtUtils;
        this.userService = userService;
        this.changeUsernameService = changeUsernameService;
        this.changePasswordService = changePasswordService;
        this.deleteUserService = deleteUserService;
    }

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserResponse> getUser(
            @RequestHeader("Authorization") String authorization
    ) throws EntityNotFoundException {
        logger.info("Getting user account");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        return ResponseEntity.ok(userService.getUserResponse(username));
    }

    @PutMapping("/username")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<JwtResponse> changeUsername(
            @RequestHeader("Authorization") String authorization,
            @RequestBody ChangeUsernameRequest changeUsernameRequest
    ) throws EntityNotFoundException, UnauthorizedException, EntityCreationException, UsernameExistsException {
        logger.info("Changing username");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        return ResponseEntity.ok(changeUsernameService.changeUsername(username, changeUsernameRequest));
    }

    @PutMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authorization,
            @RequestBody ChangePasswordRequest changePasswordRequest
    ) throws EntityNotFoundException, UnauthorizedException, EntityCreationException {
        logger.info("Changing password");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        changePasswordService.changePassword(username, changePasswordRequest);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteUser(
            @RequestHeader("Authorization") String authorization,
            @RequestBody DeleteAccountRequest deleteAccountRequest
    ) throws EntityNotFoundException, UnauthorizedException {
        logger.info("Deleting user account");
        String username = jwtUtils.getUsernameFromAuthorizationHeader(authorization);

        deleteUserService.deleteUser(username, deleteAccountRequest);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
