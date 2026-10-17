package com.x7ubi.indexcards.service.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.x7ubi.indexcards.exceptions.AuthentikDeletionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

/**
 * Deletes a user's Authentik account through the Authentik API, using the token of the service account
 * "indexcards-backend" (see authentik/blueprints/indexcards.yaml), which may only view and delete users. The access
 * token's subject is the Authentik user's UUID (sub_mode user_uuid).
 */
@Service
public class AuthentikUserDeletionService {

    private final Logger logger = LoggerFactory.getLogger(AuthentikUserDeletionService.class);

    private final RestClient restClient;

    @Autowired
    public AuthentikUserDeletionService(
            @Value("${app.authentik.api-url}") String apiUrl, @Value("${app.authentik.api-token}") String apiToken) {
        // The call runs inside the open database transaction of the account deletion, so it must not hang.
        this(RestClient.builder().requestFactory(timeoutRequestFactory()), apiUrl, apiToken);
    }

    private static JdkClientHttpRequestFactory timeoutRequestFactory() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
        factory.setReadTimeout(Duration.ofSeconds(10));
        return factory;
    }

    public AuthentikUserDeletionService(RestClient.Builder builder, String apiUrl, String apiToken) {
        this.restClient = builder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .build();
    }

    /**
     * A user that doesn't exist (anymore) counts as deleted. Authentik superusers are never deleted, so an operator who
     * also uses the app keeps their admin login.
     */
    public void deleteUser(String oidcSubject) throws AuthentikDeletionException {
        try {
            UserPage page = restClient.get()
                    .uri(uri -> uri.path("/core/users/").queryParam("uuid", oidcSubject).build())
                    .retrieve()
                    .body(UserPage.class);
            // Only ever delete the account whose uuid is exactly the token subject.
            AuthentikUser user = page == null || page.results() == null ? null : page.results().stream()
                    .filter(candidate -> oidcSubject.equals(candidate.uuid()))
                    .findFirst()
                    .orElse(null);
            if (user == null) {
                logger.warn("Authentik user of the deleted account was not found");
                return;
            }
            if (user.superuser()) {
                logger.warn("Authentik superuser is not deleted together with the app account");
                return;
            }

            restClient.delete().uri("/core/users/{pk}/", user.pk()).retrieve().toBodilessEntity();
            logger.info("Authentik user was deleted");
        } catch (RestClientException e) {
            throw new AuthentikDeletionException("Authentik user could not be deleted", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record UserPage(List<AuthentikUser> results) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AuthentikUser(int pk, String uuid, @JsonProperty("is_superuser") boolean superuser) {
    }
}
