package com.x7ubi.indexcards.service.authentication;

import com.x7ubi.indexcards.response.auth.AuthConfigResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The frontend loads the OIDC settings at runtime, so the same build works for every deployment.
 */
@Service
public class AuthConfigService {

    private final AuthConfigResponse authConfigResponse;

    public AuthConfigService(
            @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuerUri,
            @Value("${app.auth.client-id}") String clientId) {
        this.authConfigResponse = new AuthConfigResponse(issuerUri, clientId);
    }

    public AuthConfigResponse getAuthConfig() {
        return authConfigResponse;
    }
}
