package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.exceptions.UsernameExistsException;
import com.x7ubi.indexcards.jwt.JwtUtils;
import com.x7ubi.indexcards.models.SecurityUser;
import com.x7ubi.indexcards.request.auth.LoginRequest;
import com.x7ubi.indexcards.request.auth.SignupRequest;
import com.x7ubi.indexcards.response.common.JwtResponse;
import com.x7ubi.indexcards.service.authentication.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    Logger logger = LoggerFactory.getLogger(AuthRestController.class);

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    public AuthRestController(AuthService authService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> signup(
            @RequestBody SignupRequest signupRequest
    ) throws UsernameExistsException {
        logger.info("Signing up new account");

        authService.registerNewUserAccount(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login(
            @RequestBody LoginRequest loginRequest
    ) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        SecurityUser userDetails = (SecurityUser) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUser().getId(), userDetails.getUsername()));
    }
}
