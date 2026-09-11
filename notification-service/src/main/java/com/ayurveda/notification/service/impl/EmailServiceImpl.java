package com.ayurveda.notification.service.impl;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.notification.config.NotificationMailProperties;
import com.ayurveda.notification.constant.NotificationMessages;
import com.ayurveda.notification.dto.request.SendEmailRequest;
import com.ayurveda.notification.dto.response.EmailSendResponse;
import com.ayurveda.notification.entity.EmailDeliveryLog;
import com.ayurveda.notification.mail.HospitalMailSenderFactory;
import com.ayurveda.notification.mail.TenantMailSettingsLookup;
import com.ayurveda.notification.mail.TenantMailSettingsLookup.HospitalSmtpAccount;
import com.ayurveda.notification.repository.EmailDeliveryLogRepository;
import com.ayurveda.notification.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends email via the hospital mailbox when tenantCode is present
 * (configured via PUT /api/v1/platform/hospitals/{hospitalId}/mail);
 * otherwise platform SMTP (Gmail/Microsoft env).
 * <p>
 * Delivery failures are logged and returned as soft status — they never throw,
 * so patient/appointment/invoice/payment APIs are never failed by email.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 400L;

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final NotificationMailProperties mailProperties;
    private final TenantMailSettingsLookup tenantMailSettingsLookup;
    private final EmailDeliveryLogRepository emailDeliveryLogRepository;

    @Override
    public ApiResponse<EmailSendResponse> sendEmail(SendEmailRequest request) {
        String to = request.getTo() != null ? request.getTo().trim() : "";
        String subject = request.getSubject() != null ? request.getSubject().trim() : "";
        String body = request.getBody() != null ? request.getBody() : "";
        String tenantCode = firstNonBlank(request.getTenantCode(), TenantContext.getTenantCode());

        if (!StringUtils.hasText(to) || !StringUtils.hasText(subject)) {
            return softResult(
                    NotificationMessages.EMAIL_QUEUED,
                    "SKIPPED",
                    0,
                    to,
                    tenantCode,
                    "Missing recipient or subject");
        }

        if (!mailProperties.isEnabled()) {
            log.info("Email disabled; would send to={} subject={}", to, subject);
            return softResult(
                    NotificationMessages.EMAIL_QUEUED,
                    "SKIPPED",
                    0,
                    to,
                    tenantCode,
                    "Email disabled");
        }

        log.info("Email send requested to={} tenantCode={}", to, tenantCode);
        HospitalSmtpAccount hospitalMail = tenantMailSettingsLookup.findByTenantCode(tenantCode)
                .filter(HospitalSmtpAccount::ready)
                .orElse(null);
        if (hospitalMail != null) {
            return attemptSend(
                    HospitalMailSenderFactory.create(hospitalMail),
                    hospitalMail.fromEmail(),
                    hospitalMail.provider(),
                    to,
                    subject,
                    body,
                    tenantCode);
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.info(
                    "No JavaMailSender configured (provider={} smtpReady={}); logging email to={} subject={}",
                    mailProperties.effectiveProvider(),
                    mailProperties.smtpReady(),
                    to,
                    subject);
            return softResult(
                    NotificationMessages.EMAIL_QUEUED,
                    "SKIPPED",
                    0,
                    to,
                    tenantCode,
                    "No SMTP sender configured");
        }

        return attemptSend(
                mailSender,
                mailProperties.resolveFrom(),
                String.valueOf(mailProperties.effectiveProvider()),
                to,
                subject,
                body,
                tenantCode);
    }

    private ApiResponse<EmailSendResponse> attemptSend(
            JavaMailSender mailSender,
            String from,
            String provider,
            String to,
            String subject,
            String body,
            String tenantCode) {

        String lastError = null;
        int attempts = 0;
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            attempts = i;
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                if (StringUtils.hasText(from)) {
                    message.setFrom(from.trim());
                }
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                log.info("Email sent via SMTP provider={} to={} attempt={}", provider, to, attempts);
                persistLog(to, tenantCode, subject, "SENT", attempts, provider, null);
                return softResult(
                        NotificationMessages.EMAIL_SENT,
                        "SENT",
                        attempts,
                        to,
                        tenantCode,
                        null);
            } catch (Exception ex) {
                lastError = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getSimpleName();
                log.warn(
                        "Email attempt {}/{} failed via SMTP provider={} to={}: {}",
                        attempts,
                        MAX_ATTEMPTS,
                        provider,
                        to,
                        lastError);
                if (i < MAX_ATTEMPTS) {
                    sleepQuietly(RETRY_DELAY_MS);
                }
            }
        }

        String truncated = lastError != null && lastError.length() > 500
                ? lastError.substring(0, 500)
                : lastError;
        persistLog(to, tenantCode, subject, "FAILED", attempts, provider, truncated);
        log.warn(
                "Email not delivered after {} attempts to={} tenantCode={} — callers are not failed",
                attempts,
                to,
                tenantCode);
        return softResult(
                NotificationMessages.EMAIL_NOT_DELIVERED,
                "FAILED",
                attempts,
                to,
                tenantCode,
                truncated);
    }

    private ApiResponse<EmailSendResponse> softResult(
            String message,
            String deliveryStatus,
            int attempts,
            String to,
            String tenantCode,
            String errorMessage) {
        // Always success=true / HTTP 200 so RestTemplate and business APIs never fail on email.
        return ApiResponse.success(
                message,
                EmailSendResponse.builder()
                        .deliveryStatus(deliveryStatus)
                        .attempts(attempts)
                        .to(to)
                        .tenantCode(tenantCode)
                        .errorMessage(errorMessage)
                        .build());
    }

    private void persistLog(
            String to,
            String tenantCode,
            String subject,
            String status,
            int attempts,
            String provider,
            String errorMessage) {
        try {
            emailDeliveryLogRepository.save(EmailDeliveryLog.builder()
                    .recipientEmail(to)
                    .tenantCode(tenantCode)
                    .subject(subject != null && subject.length() > 200 ? subject.substring(0, 200) : subject)
                    .deliveryStatus(status)
                    .attempts(attempts)
                    .provider(provider)
                    .errorMessage(errorMessage)
                    .build());
        } catch (Exception ex) {
            log.warn("Could not persist email delivery log: {}", ex.getMessage());
        }
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private static String firstNonBlank(String a, String b) {
        if (StringUtils.hasText(a)) {
            return a.trim();
        }
        if (StringUtils.hasText(b)) {
            return b.trim();
        }
        return null;
    }
}
