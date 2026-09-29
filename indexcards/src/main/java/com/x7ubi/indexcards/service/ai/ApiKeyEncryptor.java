package com.x7ubi.indexcards.service.ai;

import com.x7ubi.indexcards.config.AiProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Encrypts the users' Gemini API keys with AES-256-GCM before they are stored. The user id is bound as associated
 * data, so an encrypted key copied to another user's row cannot be decrypted. Stored format:
 * {@code v1:<Base64(12-byte IV || ciphertext+tag)>}.
 */
@Component
public class ApiKeyEncryptor {

    private static final String PREFIX = "v1:";

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private static final int IV_BYTES = 12;

    private static final int TAG_BITS = 128;

    private final SecretKeySpec key;

    private final SecureRandom random = new SecureRandom();

    public ApiKeyEncryptor(AiProperties properties) {
        if (!StringUtils.hasText(properties.encryptionSecret())) {
            this.key = null;
            return;
        }
        byte[] secret;
        try {
            secret = Base64.getDecoder().decode(properties.encryptionSecret().strip());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("AI_KEY_ENCRYPTION_SECRET must be Base64, e.g. `openssl rand -base64 32`");
        }
        if (secret.length != 32) {
            throw new IllegalStateException("AI_KEY_ENCRYPTION_SECRET must decode to exactly 32 bytes, "
                    + "e.g. `openssl rand -base64 32`");
        }
        this.key = new SecretKeySpec(secret, "AES");
    }

    /**
     * @return false if no encryption secret is configured; the AI feature is then disabled.
     */
    public boolean isAvailable() {
        return key != null;
    }

    public String encrypt(String plaintext, long userId) {
        requireAvailable();
        try {
            byte[] iv = new byte[IV_BYTES];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            cipher.updateAAD(associatedData(userId));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] stored = ByteBuffer.allocate(iv.length + ciphertext.length).put(iv).put(ciphertext).array();
            return PREFIX + Base64.getEncoder().encodeToString(stored);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Encrypting the API key failed", e);
        }
    }

    /**
     * @throws GeneralSecurityException if the value was encrypted with another secret, for another user, or tampered
     *                                  with
     */
    public String decrypt(String stored, long userId) throws GeneralSecurityException {
        requireAvailable();
        if (stored == null || !stored.startsWith(PREFIX)) {
            throw new AEADBadTagException("Unknown encrypted API key format");
        }
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(stored.substring(PREFIX.length()));
        } catch (IllegalArgumentException e) {
            throw new AEADBadTagException("Encrypted API key is not Base64");
        }
        if (bytes.length <= IV_BYTES) {
            throw new AEADBadTagException("Encrypted API key is too short");
        }
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, bytes, 0, IV_BYTES));
        cipher.updateAAD(associatedData(userId));
        byte[] plaintext = cipher.doFinal(bytes, IV_BYTES, bytes.length - IV_BYTES);
        return new String(plaintext, StandardCharsets.UTF_8);
    }

    private void requireAvailable() {
        if (key == null) {
            throw new IllegalStateException("No AI_KEY_ENCRYPTION_SECRET configured");
        }
    }

    private static byte[] associatedData(long userId) {
        return ("indexcards-user:" + userId).getBytes(StandardCharsets.UTF_8);
    }
}
