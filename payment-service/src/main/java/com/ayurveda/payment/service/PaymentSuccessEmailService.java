package com.ayurveda.payment.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ayurveda.common.notification.EmailNotificationPublisher;
import com.ayurveda.payment.entity.PaymentTransaction;
import com.ayurveda.payment.enums.PaymentStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Emails the patient when PayU reports success (hospital SMTP via notification-service).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessEmailService {

    private final EmailNotificationPublisher emailNotificationPublisher;

    public void notifyIfNeeded(PaymentTransaction payment, PaymentStatus previousStatus, PaymentStatus mapped) {
        if (payment == null || mapped != PaymentStatus.SUCCESS) {
            return;
        }
        if (previousStatus == PaymentStatus.SUCCESS) {
            return;
        }
        String to = payment.getEmail();
        if (to == null || to.isBlank()) {
            log.warn("Skip payment-success email: missing patient email. txnid={}", payment.getPayuTxnId());
            return;
        }

        String name = payment.getFirstName() != null && !payment.getFirstName().isBlank()
                ? payment.getFirstName().trim()
                : "Patient";
        BigDecimal amount = payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO;
        String txn = payment.getPayuTxnId() != null ? payment.getPayuTxnId() : "";
        String invoice = payment.getInvoiceId() != null
                ? String.valueOf(payment.getInvoiceId())
                : (payment.getProductInfo() != null && !payment.getProductInfo().isBlank()
                        ? payment.getProductInfo().trim()
                        : "-");

        String subject = "Payment received — Ayurvedaa";
        String body = """
                Hello %s,

                We received your online payment of INR %s.

                Invoice: %s
                Transaction reference: %s

                Thank you. You can keep this email as your payment acknowledgement.
                """.formatted(name, amount.toPlainString(), invoice, txn);

        emailNotificationPublisher.sendEmail(to, subject, body, payment.getTenantCode());
        log.info("Payment-success email queued for txnid={} to={}", txn, to);
    }
}
