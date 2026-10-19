package com.x7ubi.indexcards.service.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

/**
 * Calls the Authentik API with the token of the service account "indexcards-backend" (see
 * authentik/blueprints/indexcards.yaml), whose role only allows the operations the backend needs. The access token's
 * subject is the Authentik user's UUID (sub_mode user_uuid).
 */
public abstract class AbstractAuthentikApiService {

    private final Logger logger = LoggerFactory.getLogger(AbstractAuthentikApiService.class);

    protected final RestClient restClient;

    protected AbstractAuthentikApiService(RestClient.Builder builder, String apiUrl, String apiToken) {
        if (apiToken == null || apiToken.isBlank()) {
            logger.warn("AUTHENTIK_API_TOKEN is not set; requests to the Authentik API will fail");
        }
        this.restClient = builder
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiToken)
                .build();
    }

    /**
     * Calls can run inside open database transactions, so they must not hang.
     */
    protected static RestClient.Builder timeoutRestClientBuilder() {
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build());
        factory.setReadTimeout(Duration.ofSeconds(10));
        return RestClient.builder().requestFactory(factory);
    }

    /**
     * Only ever returns the user whose uuid is exactly the token subject.
     */
    protected Optional<AuthentikUser> findUser(String oidcSubject) {
        UserPage page = restClient.get()
                .uri(uri -> uri.path("/core/users/").queryParam("uuid", oidcSubject).build())
                .retrieve()
                .body(UserPage.class);
        if (page == null || page.results() == null) {
            return Optional.empty();
        }
        return page.results().stream()
                .filter(candidate -> oidcSubject.equals(candidate.uuid()))
                .findFirst();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record UserPage(List<AuthentikUser> results) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    protected record AuthentikUser(int pk, String uuid, @JsonProperty("is_superuser") boolean superuser) {
    }
}
