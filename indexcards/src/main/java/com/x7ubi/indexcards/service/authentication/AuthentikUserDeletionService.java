package com.x7ubi.indexcards.service.authentication;

import com.x7ubi.indexcards.exceptions.AuthentikDeletionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Deletes a user's Authentik account through the Authentik API.
 */
@Service
public class AuthentikUserDeletionService extends AbstractAuthentikApiService {

    private final Logger logger = LoggerFactory.getLogger(AuthentikUserDeletionService.class);

    @Autowired
    public AuthentikUserDeletionService(
            @Value("${app.authentik.api-url}") String apiUrl, @Value("${app.authentik.api-token}") String apiToken) {
        // The call runs inside the open database transaction of the account deletion.
        this(timeoutRestClientBuilder(), apiUrl, apiToken);
    }

    public AuthentikUserDeletionService(RestClient.Builder builder, String apiUrl, String apiToken) {
        super(builder, apiUrl, apiToken);
    }

    /**
     * A user that doesn't exist (anymore) counts as deleted. Authentik superusers are never deleted, so an operator who
     * also uses the app keeps their admin login.
     */
    public void deleteUser(String oidcSubject) throws AuthentikDeletionException {
        try {
            AuthentikUser user = findUser(oidcSubject).orElse(null);
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
}
