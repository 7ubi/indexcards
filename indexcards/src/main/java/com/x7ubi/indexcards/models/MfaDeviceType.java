package com.x7ubi.indexcards.models;

import java.util.Arrays;
import java.util.Optional;

/**
 * Second factors a user can set up (see authentik/blueprints/indexcards.yaml). {@code modelName} is the device's
 * {@code meta_model_name} in the Authentik API, {@code apiPath} its admin endpoint under {@code /authenticators/admin/}.
 */
public enum MfaDeviceType {
    TOTP("totp", "authentik_stages_authenticator_totp.totpdevice", "totp"),
    WEBAUTHN("webauthn", "authentik_stages_authenticator_webauthn.webauthndevice", "webauthn"),
    EMAIL("email", "authentik_stages_authenticator_email.emaildevice", "email");

    private final String key;

    private final String modelName;

    private final String apiPath;

    MfaDeviceType(String key, String modelName, String apiPath) {
        this.key = key;
        this.modelName = modelName;
        this.apiPath = apiPath;
    }

    public String getKey() {
        return key;
    }

    public String getApiPath() {
        return apiPath;
    }

    public static Optional<MfaDeviceType> fromKey(String key) {
        return Arrays.stream(values()).filter(type -> type.key.equals(key)).findFirst();
    }

    public static Optional<MfaDeviceType> fromModelName(String modelName) {
        return Arrays.stream(values()).filter(type -> type.modelName.equals(modelName)).findFirst();
    }
}
