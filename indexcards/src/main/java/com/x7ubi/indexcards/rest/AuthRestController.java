package com.x7ubi.indexcards.rest;

import com.x7ubi.indexcards.request.SignupRequest;
import com.x7ubi.indexcards.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    Logger logger = LoggerFactory.getLogger(AuthRestController.class);

    @Autowired
    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> Signup(
            @RequestBody SignupRequest signupRequest
    ) {
        logger.info("Signing up new account");
        if(authService.registerNewUserAccount(signupRequest) != null) {
            return new ResponseEntity<>("Account created", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User with this Username already exists", HttpStatus.BAD_REQUEST);
        }
    }
}
