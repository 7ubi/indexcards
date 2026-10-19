package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.AuthentikDeletionException;
import com.x7ubi.indexcards.exceptions.AuthentikMfaException;
import com.x7ubi.indexcards.exceptions.EntityCreationException;
import com.x7ubi.indexcards.exceptions.EntityNotFoundException;
import com.x7ubi.indexcards.exceptions.UnauthorizedException;
import com.x7ubi.indexcards.exceptions.UsernameExistsException;
import com.x7ubi.indexcards.models.SecurityUser;
import com.x7ubi.indexcards.request.user.ChangeUsernameRequest;
import com.x7ubi.indexcards.request.user.DeleteAccountRequest;
import com.x7ubi.indexcards.response.user.MfaDeviceResponse;
import com.x7ubi.indexcards.response.user.UserResponse;
import com.x7ubi.indexcards.service.user.ChangeUsernameService;
import com.x7ubi.indexcards.service.user.DeleteMfaDeviceService;
import com.x7ubi.indexcards.service.user.DeleteUserService;
import com.x7ubi.indexcards.service.user.MfaDeviceService;
import com.x7ubi.indexcards.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserRestController {
    Logger logger = LoggerFactory.getLogger(UserRestController.class);

    private final UserService userService;

    private final ChangeUsernameService changeUsernameService;

    private final DeleteUserService deleteUserService;

    private final MfaDeviceService mfaDeviceService;

    private final DeleteMfaDeviceService deleteMfaDeviceService;

    public UserRestController(
            UserService userService, ChangeUsernameService changeUsernameService,
            DeleteUserService deleteUserService, MfaDeviceService mfaDeviceService,
            DeleteMfaDeviceService deleteMfaDeviceService) {
        this.userService = userService;
        this.changeUsernameService = changeUsernameService;
        this.deleteUserService = deleteUserService;
        this.mfaDeviceService = mfaDeviceService;
        this.deleteMfaDeviceService = deleteMfaDeviceService;
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

    @GetMapping("/mfa")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<MfaDeviceResponse>> getMfaDevices(
            @AuthenticationPrincipal SecurityUser securityUser
    ) throws EntityNotFoundException, AuthentikMfaException {
        logger.info("Getting 2FA devices");
        String username = securityUser.getUsername();

        return ResponseEntity.ok(mfaDeviceService.getMfaDevices(username));
    }

    @DeleteMapping("/mfa/{type}/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteMfaDevice(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable String type,
            @PathVariable String deviceId
    ) throws EntityNotFoundException, AuthentikMfaException {
        logger.info("Removing 2FA device");
        String username = securityUser.getUsername();

        deleteMfaDeviceService.deleteMfaDevice(username, type, deviceId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
