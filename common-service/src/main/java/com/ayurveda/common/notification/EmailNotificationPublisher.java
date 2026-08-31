package com.ayurveda.common.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * Publishes outbound emails via notification-service.
 * Failures are logged and never break the calling business flow.
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
        if (!enabled || to == null || to.isBlank() || subject == null || subject.isBlank()) {
            return;
        }

        try {
            SendEmailClientRequest payload = SendEmailClientRequest.builder()
                    .to(to.trim())
                    .subject(subject)
                    .body(body != null ? body : "")
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.postForEntity(
                    notificationBaseUrl + "/api/v1/notifications/email",
                    new HttpEntity<>(payload, headers),
                    Object.class);

            log.debug("Email notification published to {}", to);
        } catch (Exception ex) {
            log.warn("Failed to publish email to {}: {}", to, ex.getMessage());
        }
    }

}
