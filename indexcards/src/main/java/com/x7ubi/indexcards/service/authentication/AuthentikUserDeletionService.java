package com.x7ubi.indexcards.service.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.x7ubi.indexcards.exceptions.AuthentikDeletionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

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
        this(RestClient.builder(), apiUrl, apiToken);
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
            if (page == null || page.results() == null || page.results().isEmpty()) {
                logger.warn("Authentik user of the deleted account was not found");
                return;
            }

            AuthentikUser user = page.results().getFirst();
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
    record AuthentikUser(int pk, @JsonProperty("is_superuser") boolean superuser) {
    }
}
