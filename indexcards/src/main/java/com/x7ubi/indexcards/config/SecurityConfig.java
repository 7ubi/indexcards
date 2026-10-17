package com.x7ubi.indexcards.config;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.jwt.AuthEntryPointJwt;
import com.x7ubi.indexcards.models.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthEntryPointJwt unauthorizedHandler;

    private final AuthentikJwtAuthenticationConverter authentikJwtAuthenticationConverter;

    public SecurityConfig(
            AuthEntryPointJwt unauthorizedHandler,
            AuthentikJwtAuthenticationConverter authentikJwtAuthenticationConverter) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.authentikJwtAuthenticationConverter = authentikJwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(unauthorizedHandler)
                        // Answer 403 directly: the default handler forwards to /error, which is not authenticated
                        // and would turn it into a 401 (and a 401 logs the frontend out).
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                            response.getWriter().write(ErrorMessage.Authentication.FORBIDDEN);
                        }))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/images/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated())
                // Access tokens are issued by Authentik; the decoder is configured via
                // spring.security.oauth2.resourceserver.jwt.* in application.properties.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(unauthorizedHandler)
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(authentikJwtAuthenticationConverter)));

        return http.build();
    }
}
