package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.request.user.DeleteAccountRequest;
import com.x7ubi.indexcards.service.user.DeleteUserService;
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

    private final DeleteUserService deleteUserService;

    public UserRestController(JwtUtils jwtUtils, DeleteUserService deleteUserService) {
        this.jwtUtils = jwtUtils;
        this.deleteUserService = deleteUserService;
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
