package com.ayurveda.payment.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ayurveda.common.notification.EmailNotificationPublisher;
import com.ayurveda.payment.entity.PaymentTransaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRefundEmailService {

    private final EmailNotificationPublisher emailNotificationPublisher;

    public void notifyRefund(PaymentTransaction payment, BigDecimal refundAmount, String reason) {
        if (payment == null || !StringUtils.hasText(payment.getEmail())) {
            log.warn("Skip refund email: missing patient email. txnid={}",
                    payment != null ? payment.getPayuTxnId() : null);
            return;
        }
        String name = StringUtils.hasText(payment.getFirstName())
                ? payment.getFirstName().trim()
                : "Patient";
        BigDecimal amount = refundAmount != null ? refundAmount : BigDecimal.ZERO;
        String subject = "Refund processed — Ayurvedaa";
        String reasonLine = StringUtils.hasText(reason) ? reason.trim() : "Refund requested by hospital";
        String body = """
                Hello %s,

                Your online payment refund of INR %s has been initiated.

                Reason: %s
                Transaction reference: %s
                PayU id: %s

                The amount will be credited back to the original payment method as per bank timelines.
                If you have questions, please contact the hospital with this reference.
                """.formatted(
                name,
                amount.toPlainString(),
                reasonLine,
                payment.getPayuTxnId() != null ? payment.getPayuTxnId() : "",
                payment.getMihpayid() != null ? payment.getMihpayid() : "");

        emailNotificationPublisher.sendEmail(
                payment.getEmail().trim(), subject, body, payment.getTenantCode());
        log.info("Refund email queued for txnid={} to={}", payment.getPayuTxnId(), payment.getEmail());
    }
}
