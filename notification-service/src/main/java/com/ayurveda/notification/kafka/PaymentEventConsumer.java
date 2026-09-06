package com.ayurveda.notification.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ayurveda.common.kafka.KafkaTopics;
import com.ayurveda.common.kafka.PaymentEvent;
import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.common.tenant.TenantSchemaNames;
import com.ayurveda.notification.dto.request.CreateNotificationRequest;
import com.ayurveda.notification.enums.NotificationPriority;
import com.ayurveda.notification.enums.NotificationType;
import com.ayurveda.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ayurveda.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.PAYMENTS, groupId = "notification-service")
    public void onPaymentEvent(String payload) {
        PaymentEvent event;
        try {
            event = objectMapper.readValue(payload, PaymentEvent.class);
        } catch (Exception ex) {
            log.warn("Ignoring unreadable payment event: {}", ex.getMessage());
            return;
        }
        if (event.getInitiatedByUserId() == null) {
            return;
        }

        if (TenantSchemaNames.isHospitalSchema(event.getSchemaName())) {
            TenantContext.set(event.getTenantId(), event.getTenantCode(), event.getSchemaName());
        }
        try {
            boolean success = "SUCCESS".equalsIgnoreCase(event.getStatus());
            String title = success ? "Payment successful" : "Payment update";
            String message = success
                    ? "PayU payment " + event.getPayuTxnId() + " of " + event.getAmount() + " succeeded."
                    : "PayU payment " + event.getPayuTxnId() + " status is " + event.getStatus() + ".";

            notificationService.createNotification(CreateNotificationRequest.builder()
                    .recipientUserId(event.getInitiatedByUserId())
                    .title(title)
                    .message(message)
                    .type(NotificationType.BILLING)
                    .priority(success ? NotificationPriority.MEDIUM : NotificationPriority.HIGH)
                    .referenceId(event.getPaymentId())
                    .referenceType("PAYMENT")
                    .build());
            log.info("Created payment notification for user {} txnid={}", event.getInitiatedByUserId(), event.getPayuTxnId());
        } finally {
            TenantContext.clear();
        }
    }
}
