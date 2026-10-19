package com.x7ubi.indexcards.service.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.x7ubi.indexcards.exceptions.AuthentikMfaException;
import com.x7ubi.indexcards.models.MfaDeviceType;
import com.x7ubi.indexcards.response.user.MfaDeviceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Lists and removes a user's second factors (authenticator app, passkey/security key, email code, recovery codes)
 * through the
 * Authentik API. Users set them up in Authentik's setup flows, which the account page links to.
 */
@Service
public class AuthentikMfaDeviceService extends AbstractAuthentikApiService {

    @Autowired
    public AuthentikMfaDeviceService(
            @Value("${app.authentik.api-url}") String apiUrl, @Value("${app.authentik.api-token}") String apiToken) {
        this(timeoutRestClientBuilder(), apiUrl, apiToken);
    }

    public AuthentikMfaDeviceService(RestClient.Builder builder, String apiUrl, String apiToken) {
        super(builder, apiUrl, apiToken);
    }

    /**
     * Only confirmed devices of the supported types; a setup that was started but not finished is not listed.
     */
    public List<MfaDeviceResponse> getDevices(String oidcSubject) throws AuthentikMfaException {
        try {
            AuthentikUser user = findUser(oidcSubject).orElse(null);
            if (user == null) {
                return List.of();
            }

            AuthentikDevice[] devices = restClient.get()
                    .uri(uri -> uri.path("/authenticators/admin/all/").queryParam("user", user.pk()).build())
                    .retrieve()
                    .body(AuthentikDevice[].class);
            if (devices == null) {
                return List.of();
            }

            return Arrays.stream(devices)
                    .filter(AuthentikDevice::confirmed)
                    .map(device -> MfaDeviceType.fromModelName(device.modelName())
                            .map(type -> new MfaDeviceResponse(
                                    type.getKey(), device.pk(), device.name(), device.created(), device.lastUsed()))
                            .orElse(null))
                    .filter(Objects::nonNull)
                    .toList();
        } catch (RestClientException e) {
            throw new AuthentikMfaException("2FA devices could not be loaded from Authentik", e);
        }
    }

    /**
     * Deletes the device only if it belongs to the user, so a user can never remove someone else's device.
     *
     * @return {@code false} if the user has no such device
     */
    public boolean deleteDevice(String oidcSubject, MfaDeviceType type, String deviceId) throws AuthentikMfaException {
        boolean ownDevice = getDevices(oidcSubject).stream()
                .anyMatch(device -> device.getType().equals(type.getKey()) && device.getId().equals(deviceId));
        if (!ownDevice) {
            return false;
        }

        try {
            restClient.delete()
                    .uri("/authenticators/admin/{path}/{id}/", type.getApiPath(), deviceId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (RestClientException e) {
            throw new AuthentikMfaException("2FA device could not be deleted in Authentik", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AuthentikDevice(
            String pk, String name, @JsonProperty("meta_model_name") String modelName, boolean confirmed,
            String created, @JsonProperty("last_used") String lastUsed) {
    }
}
