package com.ayurveda.payment.service;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayurveda.common.notification.EmailNotificationPublisher;
import com.ayurveda.payment.entity.PaymentTransaction;
import com.ayurveda.payment.enums.PaymentStatus;

@ExtendWith(MockitoExtension.class)
class PaymentSuccessEmailServiceTest {

    @Mock
    private EmailNotificationPublisher emailNotificationPublisher;

    @InjectMocks
    private PaymentSuccessEmailService service;

    @Test
    void sendsEmailOnFirstSuccess() {
        PaymentTransaction payment = PaymentTransaction.builder()
                .payuTxnId("TXN-1")
                .amount(new BigDecimal("10.00"))
                .firstName("Ravi")
                .email("ravi@example.com")
                .tenantCode("GAN-DL")
                .invoiceId(UUID.randomUUID())
                .build();

        service.notifyIfNeeded(payment, PaymentStatus.INITIATED, PaymentStatus.SUCCESS);

        verify(emailNotificationPublisher).sendEmail(
                eq("ravi@example.com"),
                eq("Payment received — Ayurvedaa"),
                anyString(),
                eq("GAN-DL"));
    }

    @Test
    void skipsWhenAlreadySuccess() {
        PaymentTransaction payment = PaymentTransaction.builder()
                .payuTxnId("TXN-1")
                .email("ravi@example.com")
                .amount(BigDecimal.TEN)
                .firstName("Ravi")
                .build();

        service.notifyIfNeeded(payment, PaymentStatus.SUCCESS, PaymentStatus.SUCCESS);

        verify(emailNotificationPublisher, never()).sendEmail(anyString(), anyString(), anyString(), anyString());
    }
}
