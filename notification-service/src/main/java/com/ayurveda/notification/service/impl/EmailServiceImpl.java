package com.ayurveda.notification.service.impl;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.notification.constant.NotificationMessages;
import com.ayurveda.notification.dto.request.SendEmailRequest;
import com.ayurveda.notification.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sends email via Spring {@link JavaMailSender} when SMTP is configured;
 * otherwise logs the message (local/dev without mail host).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${notification.mail.from:noreply@ayurvedaa.local}")
    private String fromAddress;

    @Value("${notification.mail.enabled:true}")
    private boolean mailEnabled;

    @Override
    public ApiResponse<Void> sendEmail(SendEmailRequest request) {
        String to = request.getTo().trim();
        String subject = request.getSubject().trim();
        String body = request.getBody();

        if (!mailEnabled) {
            log.info("Email disabled; would send to={} subject={}", to, subject);
            return ApiResponse.success(NotificationMessages.EMAIL_QUEUED, null);
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.info("No JavaMailSender configured; logging email to={} subject={} body=\n{}",
                    to, subject, body);
            return ApiResponse.success(NotificationMessages.EMAIL_QUEUED, null);
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            if (StringUtils.hasText(fromAddress)) {
                message.setFrom(fromAddress);
            }
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } catch (Exception ex) {
            log.warn("Failed to send email to {}: {}", to, ex.getMessage());
            // Still succeed at API level so callers (forgot-password) are not broken by SMTP outages
        }

        return ApiResponse.success(NotificationMessages.EMAIL_SENT, null);
    }

}
