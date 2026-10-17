package com.x7ubi.indexcards.config;

import com.x7ubi.indexcards.models.SecurityUser;
import com.x7ubi.indexcards.models.User;
import com.x7ubi.indexcards.service.admin.AdminGroupService;
import com.x7ubi.indexcards.service.authentication.OidcUserProvisioningService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Turns a validated Authentik access token into the local user, so controllers keep receiving a {@link SecurityUser}
 * and the role ({@code ROLE_ADMIN}) still comes from the database.
 */
@Component
public class AuthentikJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final OidcUserProvisioningService oidcUserProvisioningService;

    private final AdminGroupService adminGroupService;

    public AuthentikJwtAuthenticationConverter(OidcUserProvisioningService oidcUserProvisioningService,
                                               AdminGroupService adminGroupService) {
        this.oidcUserProvisioningService = oidcUserProvisioningService;
        this.adminGroupService = adminGroupService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        User user;
        try {
            user = adminGroupService.grantAdminFromGroups(oidcUserProvisioningService.getOrCreateUser(jwt), jwt);
        } catch (AuthenticationException e) {
            throw e;
        } catch (RuntimeException e) {
            // E.g. a database outage: the token is fine, so this must not look like a 401 (which logs the user out).
            throw new AuthenticationServiceException("Could not load the user of the token", e);
        }
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, jwt, securityUser.getAuthorities());
    }
}
