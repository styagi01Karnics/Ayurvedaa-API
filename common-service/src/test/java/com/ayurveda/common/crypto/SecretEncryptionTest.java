package com.ayurveda.common.crypto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SecretEncryptionTest {

    private final SecretEncryption encryption =
            new SecretEncryption("unit-test-master-key-at-least-32-chars");

    @Test
    void encryptDecryptRoundTrip() {
        String encrypted = encryption.encrypt("S@b8at1ze0108");
        assertTrue(encrypted.startsWith(SecretEncryption.PREFIX));
        assertNotEquals("S@b8at1ze0108", encrypted);
        assertEquals("S@b8at1ze0108", encryption.decrypt(encrypted));
    }

    @Test
    void eachEncryptUsesFreshSalt() {
        String a = encryption.encrypt("same-password");
        String b = encryption.encrypt("same-password");
        assertNotEquals(a, b);
        assertEquals("same-password", encryption.decrypt(a));
        assertEquals("same-password", encryption.decrypt(b));
    }

    @Test
    void legacyPlaintextPassesThroughDecrypt() {
        assertFalse(encryption.isEncrypted("plain-smtp-password"));
        assertEquals("plain-smtp-password", encryption.decrypt("plain-smtp-password"));
    }

    @Test
    void encryptIsIdempotentOnAlreadyEncryptedValue() {
        String once = encryption.encrypt("secret");
        assertEquals(once, encryption.encrypt(once));
    }
}
