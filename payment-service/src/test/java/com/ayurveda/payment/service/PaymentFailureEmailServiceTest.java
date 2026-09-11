package com.ayurveda.payment.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayurveda.common.notification.EmailNotificationPublisher;
import com.ayurveda.payment.entity.PaymentTransaction;
import com.ayurveda.payment.enums.PaymentStatus;

@ExtendWith(MockitoExtension.class)
class PaymentFailureEmailServiceTest {

    @Mock
    private EmailNotificationPublisher emailNotificationPublisher;

    @InjectMocks
    private PaymentFailureEmailService service;

    @Test
    void sendsEmailOnFirstFailure() {
        PaymentTransaction payment = PaymentTransaction.builder()
                .payuTxnId("TXN1")
                .amount(new BigDecimal("100.00"))
                .firstName("Ravi")
                .email("patient@example.com")
                .tenantCode("GAN-DL")
                .errorMessage("Bank declined")
                .build();

        service.notifyIfNeeded(payment, PaymentStatus.INITIATED, PaymentStatus.FAILED);

        verify(emailNotificationPublisher).sendEmail(
                eq("patient@example.com"),
                eq("Payment failed — Ayurvedaa"),
                anyString(),
                eq("GAN-DL"));
    }

    @Test
    void skipsDuplicateFailureEmail() {
        PaymentTransaction payment = PaymentTransaction.builder()
                .email("patient@example.com")
                .build();

        service.notifyIfNeeded(payment, PaymentStatus.FAILED, PaymentStatus.FAILED);

        verifyNoInteractions(emailNotificationPublisher);
    }

    @Test
    void skipsSuccess() {
        PaymentTransaction payment = PaymentTransaction.builder()
                .email("patient@example.com")
                .build();

        service.notifyIfNeeded(payment, PaymentStatus.INITIATED, PaymentStatus.SUCCESS);

        verify(emailNotificationPublisher, never()).sendEmail(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void toleratesMissingEmail() {
        PaymentTransaction payment = PaymentTransaction.builder()
                .payuTxnId("TXN2")
                .build();

        assertDoesNotThrow(() ->
                service.notifyIfNeeded(payment, PaymentStatus.PENDING, PaymentStatus.CANCELLED));
        verifyNoInteractions(emailNotificationPublisher);
    }
}
