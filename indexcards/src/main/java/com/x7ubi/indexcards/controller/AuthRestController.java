package com.x7ubi.indexcards.controller;

import com.x7ubi.indexcards.response.auth.AuthConfigResponse;
import com.x7ubi.indexcards.service.authentication.AuthConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthConfigService authConfigService;

    public AuthRestController(AuthConfigService authConfigService) {
        this.authConfigService = authConfigService;
    }

    @GetMapping("/config")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthConfigResponse> getAuthConfig() {
        return ResponseEntity.ok(authConfigService.getAuthConfig());
    }
}
