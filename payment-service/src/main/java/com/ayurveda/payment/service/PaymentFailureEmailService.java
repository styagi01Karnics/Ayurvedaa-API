package com.ayurveda.payment.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ayurveda.common.notification.EmailNotificationPublisher;
import com.ayurveda.payment.entity.PaymentTransaction;
import com.ayurveda.payment.enums.PaymentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Emails the patient when PayU reports failure/cancel (hospital SMTP via notification-service).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentFailureEmailService {

    private final EmailNotificationPublisher emailNotificationPublisher;

    public void notifyIfNeeded(PaymentTransaction payment, PaymentStatus previousStatus, PaymentStatus mapped) {
        if (payment == null || mapped == null) {
            return;
        }
        if (mapped != PaymentStatus.FAILED && mapped != PaymentStatus.CANCELLED) {
            return;
        }
        if (previousStatus == PaymentStatus.SUCCESS
                || previousStatus == PaymentStatus.FAILED
                || previousStatus == PaymentStatus.CANCELLED) {
            return;
        }
        String to = payment.getEmail();
        if (to == null || to.isBlank()) {
            log.warn("Skip payment-failed email: missing patient email. txnid={}", payment.getPayuTxnId());
            return;
        }

        String name = payment.getFirstName() != null && !payment.getFirstName().isBlank()
                ? payment.getFirstName().trim()
                : "Patient";
        BigDecimal amount = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
        String txn = payment.getPayuTxnId() != null ? payment.getPayuTxnId() : "";
        String reason = payment.getErrorMessage() != null && !payment.getErrorMessage().isBlank()
                ? payment.getErrorMessage().trim()
                : (mapped == PaymentStatus.CANCELLED ? "Payment was cancelled." : "Payment was not completed.");

        String subject = "Payment failed — Ayurvedaa";
        String body = """
                Hello %s,

                Your online payment of INR %s was not successful.

                Reason: %s
                Transaction reference: %s

                Please try again using the payment link, scan the clinic QR, or pay cash at the hospital.

                If the amount was deducted from your account, contact the hospital with this reference.
                """.formatted(name, amount.toPlainString(), reason, txn);

        emailNotificationPublisher.sendEmail(to, subject, body, payment.getTenantCode());
        log.info("Payment-failed email queued for txnid={} to={}", txn, to);
    }
}
