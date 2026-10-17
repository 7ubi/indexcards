package com.x7ubi.indexcards.config;

import com.x7ubi.indexcards.models.SecurityUser;
import com.x7ubi.indexcards.service.authentication.OidcUserProvisioningService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Turns a validated Authentik access token into the local user, so controllers keep receiving a {@link SecurityUser}
 * and the role ({@code ROLE_ADMIN}) still comes from the database.
 */
@Component
public class AuthentikJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final OidcUserProvisioningService oidcUserProvisioningService;

    public AuthentikJwtAuthenticationConverter(OidcUserProvisioningService oidcUserProvisioningService) {
        this.oidcUserProvisioningService = oidcUserProvisioningService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        SecurityUser securityUser = new SecurityUser(oidcUserProvisioningService.getOrCreateUser(jwt));
        return new UsernamePasswordAuthenticationToken(securityUser, jwt, securityUser.getAuthorities());
    }
}
