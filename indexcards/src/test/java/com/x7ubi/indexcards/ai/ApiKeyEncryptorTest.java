package com.x7ubi.indexcards.ai;

import com.x7ubi.indexcards.config.AiProperties;
import com.x7ubi.indexcards.service.ai.ApiKeyEncryptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.Base64;

public class ApiKeyEncryptorTest {

    private static final String API_KEY = "AIzaSyAbcdefghijklmnopqrstuvwxyz0123456789";

    static AiProperties properties(String secret) {
        return new AiProperties(secret, "gemini-3.8-flash", "low", 30, 20000, 10_485_760, 20, 5, 0, 0, 16000, "");
    }

    static String secret(int fill) {
        byte[] key = new byte[32];
        java.util.Arrays.fill(key, (byte) fill);
        return Base64.getEncoder().encodeToString(key);
    }

    private final ApiKeyEncryptor encryptor = new ApiKeyEncryptor(properties(secret(1)));

    @Test
    public void roundTripTest() throws GeneralSecurityException {
        String stored = encryptor.encrypt(API_KEY, 7);

        Assertions.assertTrue(stored.startsWith("v1:"));
        Assertions.assertFalse(stored.contains(API_KEY));
        Assertions.assertEquals(API_KEY, encryptor.decrypt(stored, 7));
    }

    @Test
    public void everyEncryptionUsesAFreshIvTest() {
        Assertions.assertNotEquals(encryptor.encrypt(API_KEY, 7), encryptor.encrypt(API_KEY, 7));
    }

    @Test
    public void keyOfAnotherUserCannotBeDecryptedTest() {
        String stored = encryptor.encrypt(API_KEY, 7);

        Assertions.assertThrows(GeneralSecurityException.class, () -> encryptor.decrypt(stored, 8));
    }

    @Test
    public void otherSecretCannotDecryptTest() {
        String stored = encryptor.encrypt(API_KEY, 7);
        ApiKeyEncryptor other = new ApiKeyEncryptor(properties(secret(2)));

        Assertions.assertThrows(GeneralSecurityException.class, () -> other.decrypt(stored, 7));
    }

    @Test
    public void tamperedValueIsRejectedTest() {
        String stored = encryptor.encrypt(API_KEY, 7);
        byte[] bytes = Base64.getDecoder().decode(stored.substring(3));
        bytes[bytes.length - 1] ^= 1;
        String tampered = "v1:" + Base64.getEncoder().encodeToString(bytes);

        Assertions.assertThrows(GeneralSecurityException.class, () -> encryptor.decrypt(tampered, 7));
        Assertions.assertThrows(GeneralSecurityException.class, () -> encryptor.decrypt("plain-text", 7));
    }

    @Test
    public void withoutSecretTheFeatureIsUnavailableTest() {
        ApiKeyEncryptor disabled = new ApiKeyEncryptor(properties(""));

        Assertions.assertFalse(disabled.isAvailable());
        Assertions.assertThrows(IllegalStateException.class, () -> disabled.encrypt(API_KEY, 1));
    }

    @Test
    public void secretWithWrongLengthFailsAtStartupTest() {
        String shortSecret = Base64.getEncoder().encodeToString(new byte[16]);

        Assertions.assertThrows(IllegalStateException.class, () -> new ApiKeyEncryptor(properties(shortSecret)));
        Assertions.assertThrows(IllegalStateException.class, () -> new ApiKeyEncryptor(properties("not base64!")));
    }
}
