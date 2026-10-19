package com.x7ubi.indexcards.config;

import com.x7ubi.indexcards.error.ErrorMessage;
import com.x7ubi.indexcards.jwt.AuthEntryPointJwt;
import com.x7ubi.indexcards.models.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
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
                        // Docker health check; nginx only proxies /api/, so this is not reachable from outside.
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/images/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated())
                // Access tokens are issued by Authentik; the decoder is configured via
                // spring.security.oauth2.resourceserver.jwt.* in application.properties.
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(unauthorizedHandler)
                        // Bad tokens answer 401 (the frontend logs out); an internal failure while loading the user
                        // (AuthenticationServiceException) answers 503 and keeps the user logged in.
                        .withObjectPostProcessor(new ObjectPostProcessor<BearerTokenAuthenticationFilter>() {
                            @Override
                            public <O extends BearerTokenAuthenticationFilter> O postProcess(O filter) {
                                filter.setAuthenticationFailureHandler((request, response, exception) -> {
                                    if (exception instanceof AuthenticationServiceException) {
                                        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                                    } else {
                                        unauthorizedHandler.commence(request, response, exception);
                                    }
                                });
                                return filter;
                            }
                        })
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(authentikJwtAuthenticationConverter)));

        return http.build();
    }
}
