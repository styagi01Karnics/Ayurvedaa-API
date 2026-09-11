package com.ayurveda.auth.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ayurveda.auth.entity.Tenant;
import com.ayurveda.auth.entity.TenantMailSettings;
import com.ayurveda.auth.entity.TenantPaymentGateway;
import com.ayurveda.auth.enums.HospitalMailProvider;
import com.ayurveda.auth.enums.PaymentGatewayMode;
import com.ayurveda.auth.enums.PaymentGatewayProvider;
import com.ayurveda.auth.mapper.AuthMapper;
import com.ayurveda.common.crypto.SecretEncryption;

class SecretMaskingTest {

    private final AuthMapper authMapper =
            new AuthMapper(new SecretEncryption("unit-test-master-key-at-least-32-chars"));

    @Test
    void masksLongSecretKeepingLastFour() {
        assertEquals("****wxyz", SecretMasking.mask("super-secret-wxyz"));
    }

    @Test
    void masksShortSecretEntirely() {
        assertEquals("****", SecretMasking.mask("ab"));
        assertNull(SecretMasking.mask(null));
        assertNull(SecretMasking.mask("  "));
    }

    @Test
    void paymentGatewayResponseNeverExposesRawSaltOrSecret() {
        TenantPaymentGateway gateway = TenantPaymentGateway.builder()
                .tenantCode("GAN-DL")
                .provider(PaymentGatewayProvider.PAYU)
                .merchantKey("testKey")
                .merchantSalt("hospital-salt-value")
                .clientId("oauth-client")
                .clientSecret("oauth-client-secret")
                .mode(PaymentGatewayMode.LIVE)
                .enabled(true)
                .build();
        gateway.setUpdatedAt(LocalDateTime.of(2026, 9, 10, 10, 0));

        var response = authMapper.toPaymentGatewayResponse(gateway);

        assertEquals("****alue", response.getMerchantSaltMasked());
        assertEquals("****cret", response.getClientSecretMasked());
        assertEquals("https://secure.payu.in/_payment", response.getResolvedPaymentUrl());
        assertEquals("testKey", response.getMerchantKey());
        assertNull(response.getPaymentUrl());
    }

    @Test
    void hospitalMailResponseNeverExposesRawPassword() {
        UUID hospitalId = UUID.fromString("2232ea8f-5398-4ce4-ba37-6b76bf683c9e");
        Tenant hospital = Tenant.builder().tenantCode("GAN-DL").build();
        hospital.setId(hospitalId);

        SecretEncryption encryption = new SecretEncryption("unit-test-master-key-at-least-32-chars");
        TenantMailSettings mail = TenantMailSettings.builder()
                .tenantCode("GAN-DL")
                .fromEmail("clinic@gmail.com")
                .smtpPassword(encryption.encrypt("gmail-app-password"))
                .provider(HospitalMailProvider.GMAIL)
                .enabled(true)
                .build();
        mail.setUpdatedAt(LocalDateTime.of(2026, 9, 10, 10, 0));

        var response = authMapper.toHospitalMailResponse(hospital, mail);

        assertEquals(hospitalId, response.getHospitalId());
        assertEquals("clinic@gmail.com", response.getEmail());
        assertEquals(HospitalMailProvider.GMAIL, response.getProvider());
        assertEquals("****word", response.getPasswordMasked());
        assertTrue(mail.getSmtpPassword().startsWith(SecretEncryption.PREFIX));
    }
}
