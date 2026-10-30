package com.ayurveda.common.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.ayurveda.common.tenant.TenantContext;

import lombok.extern.slf4j.Slf4j;

/**
 * Publishes outbound emails via notification-service.
 * Failures are logged and never break the calling business flow
 * (patient create, appointment, invoice, payment, etc. always succeed).
 */
@Slf4j
@Component
public class EmailNotificationPublisher {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${services.notification.url:http://localhost:8110}")
    private String notificationBaseUrl;

    @Value("${services.notification.enabled:true}")
    private boolean enabled;

    public void sendEmail(String to, String subject, String body) {
        sendEmail(to, subject, body, TenantContext.getTenantCode());
    }

    public void sendEmail(String to, String subject, String body, String tenantCode) {
        sendEmail(to, subject, body, tenantCode, false);
    }

    public void sendHtmlEmail(String to, String subject, String htmlBody, String tenantCode) {
        sendEmail(to, subject, htmlBody, tenantCode, true);
    }

    private void sendEmail(String to, String subject, String body, String tenantCode, boolean html) {
        if (!enabled || to == null || to.isBlank() || subject == null || subject.isBlank()) {
            return;
        }

        try {
            String resolvedTenant = (tenantCode != null && !tenantCode.isBlank())
                    ? tenantCode.trim()
                    : TenantContext.getTenantCode();
            SendEmailClientRequest payload = SendEmailClientRequest.builder()
                    .to(to.trim())
                    .subject(subject)
                    .body(body != null ? body : "")
                    .tenantCode(resolvedTenant)
                    .html(html)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.postForEntity(
                    notificationBaseUrl + "/api/v1/notifications/email",
                    new HttpEntity<>(payload, headers),
                    Object.class);

            log.info("Email notification published to={} tenantCode={}", to, resolvedTenant);
        } catch (Exception ex) {
            // Network / unexpected HTTP errors must never fail patient/appointment/billing APIs.
            log.warn("Failed to publish email to {} (business API continues): {}", to, ex.getMessage());
        }
    }

}
