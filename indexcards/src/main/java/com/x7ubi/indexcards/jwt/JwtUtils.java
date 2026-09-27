package com.x7ubi.indexcards.jwt;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;

import com.x7ubi.indexcards.models.SecurityUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import org.springframework.util.StringUtils;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;

    @Value("${bezkoder.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @PostConstruct
    void initJwtSecret() {
        if (!StringUtils.hasText(jwtSecret)) {
            // No secret configured: fall back to a random per-process key instead of a hardcoded one, so
            // tokens can never be forged with a publicly known value. Tokens are invalidated on restart.
            byte[] randomKey = new byte[64];
            new SecureRandom().nextBytes(randomKey);
            jwtSecret = Base64.getEncoder().encodeToString(randomKey);
            logger.warn("No JWT secret configured (JWT_SECRET). Using a random key; "
                    + "all issued tokens become invalid when the application restarts.");
        }
    }

    public String generateJwtToken(Authentication authentication) {

        SecurityUser userPrincipal = (SecurityUser) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUsernameFromAuthorizationHeader(String authorization) {
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return this.getUsernameFromJwtToken(authorization.split(" ")[1]);
        }

        return null;
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
