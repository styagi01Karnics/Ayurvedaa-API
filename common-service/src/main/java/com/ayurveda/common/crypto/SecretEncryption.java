package com.ayurveda.common.crypto;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-256-GCM encryption with a random per-value salt (PBKDF2) + IV.
 * Stored form: {@code ENC:v1:&lt;saltB64&gt;:&lt;ivB64&gt;:&lt;cipherB64&gt;}.
 * Values without the prefix are treated as legacy plaintext (decrypt is a no-op).
 */
public final class SecretEncryption {

    public static final String PREFIX = "ENC:v1:";

    private static final String TRANSFORM = "AES/GCM/NoPadding";
    private static final int SALT_BYTES = 16;
    private static final int IV_BYTES = 12;
    private static final int KEY_BITS = 256;
    private static final int GCM_TAG_BITS = 128;
    private static final int PBKDF2_ITERATIONS = 120_000;

    private final char[] masterSecret;
    private final SecureRandom secureRandom = new SecureRandom();

    public SecretEncryption(String masterSecret) {
        if (masterSecret == null || masterSecret.isBlank()) {
            throw new IllegalArgumentException("Secret encryption master key must not be blank");
        }
        this.masterSecret = masterSecret.toCharArray();
    }

    public boolean isEncrypted(String value) {
        return value != null && value.startsWith(PREFIX);
    }

    public String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) {
            return plaintext;
        }
        if (isEncrypted(plaintext)) {
            return plaintext;
        }
        try {
            byte[] salt = new byte[SALT_BYTES];
            secureRandom.nextBytes(salt);
            byte[] iv = new byte[IV_BYTES];
            secureRandom.nextBytes(iv);

            SecretKey key = deriveKey(salt);
            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            return PREFIX
                    + Base64.getEncoder().encodeToString(salt)
                    + ":"
                    + Base64.getEncoder().encodeToString(iv)
                    + ":"
                    + Base64.getEncoder().encodeToString(cipherText);
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Failed to encrypt secret", ex);
        }
    }

    public String decrypt(String value) {
        if (value == null || value.isBlank() || !isEncrypted(value)) {
            return value;
        }
        String payload = value.substring(PREFIX.length());
        String[] parts = payload.split(":", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid encrypted secret format");
        }
        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] iv = Base64.getDecoder().decode(parts[1]);
            byte[] cipherText = Base64.getDecoder().decode(parts[2]);

            SecretKey key = deriveKey(salt);
            Cipher cipher = Cipher.getInstance(TRANSFORM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BITS, iv));
            byte[] plain = cipher.doFinal(cipherText);
            return new String(plain, StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new IllegalStateException("Failed to decrypt secret", ex);
        }
    }

    private SecretKey deriveKey(byte[] salt) throws GeneralSecurityException {
        PBEKeySpec spec = new PBEKeySpec(masterSecret, salt, PBKDF2_ITERATIONS, KEY_BITS);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}
