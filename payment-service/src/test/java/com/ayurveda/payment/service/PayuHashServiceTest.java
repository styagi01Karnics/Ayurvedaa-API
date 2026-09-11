package com.ayurveda.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayurveda.payment.config.PayuProperties;

class PayuHashServiceTest {

    private PayuHashService hashService;

    @BeforeEach
    void setUp() {
        PayuProperties properties = new PayuProperties();
        properties.setMerchantKey("gtKFFx");
        properties.setMerchantSalt("eCwWELxi");
        hashService = new PayuHashService(properties);
    }

    @Test
    void requestHashIsStableSha512() {
        String hash = hashService.requestHash(
                "GAN-DL-PAY-00001",
                "10.00",
                "Hospital payment",
                "Admin",
                "admin@gmail.com",
                "hosp_gan_dl",
                "GAN-DL",
                "",
                "",
                "11111111-1111-1111-1111-111111111111");
        assertEquals(128, hash.length());
        assertEquals(hash, hashService.requestHash(
                "GAN-DL-PAY-00001",
                "10.00",
                "Hospital payment",
                "Admin",
                "admin@gmail.com",
                "hosp_gan_dl",
                "GAN-DL",
                "",
                "",
                "11111111-1111-1111-1111-111111111111"));
    }

    @Test
    void verifyResponseAcceptsMatchingHash() {
        String status = "success";
        String salt = "eCwWELxi";
        String raw = String.join("|",
                salt,
                status,
                "", "", "", "", "",
                "udf5",
                "udf4",
                "udf3",
                "udf2",
                "udf1",
                "admin@gmail.com",
                "Admin",
                "Hospital payment",
                "10.00",
                "GAN-DL-PAY-00001",
                "gtKFFx");
        Map<String, String> params = new LinkedHashMap<>();
        params.put("status", status);
        params.put("udf5", "udf5");
        params.put("udf4", "udf4");
        params.put("udf3", "udf3");
        params.put("udf2", "udf2");
        params.put("udf1", "udf1");
        params.put("email", "admin@gmail.com");
        params.put("firstname", "Admin");
        params.put("productinfo", "Hospital payment");
        params.put("amount", "10.00");
        params.put("txnid", "GAN-DL-PAY-00001");
        params.put("key", "gtKFFx");
        params.put("hash", PayuHashService.sha512(raw));
        assertTrue(hashService.verifyResponse(params));
        assertTrue(hashService.verifyResponse("eCwWELxi", params));
    }

    @Test
    void requestHashUsesProvidedTenantSalt() {
        String hashA = hashService.requestHash(
                "keyA",
                "saltA",
                "TXN1",
                "10.00",
                "Hospital payment",
                "Admin",
                "admin@gmail.com",
                "hosp_gan_dl",
                "GAN-DL",
                "",
                "",
                "11111111-1111-1111-1111-111111111111");
        String hashB = hashService.requestHash(
                "keyA",
                "saltB",
                "TXN1",
                "10.00",
                "Hospital payment",
                "Admin",
                "admin@gmail.com",
                "hosp_gan_dl",
                "GAN-DL",
                "",
                "",
                "11111111-1111-1111-1111-111111111111");
        assertEquals(128, hashA.length());
        assertTrue(!hashA.equals(hashB));
    }
}
