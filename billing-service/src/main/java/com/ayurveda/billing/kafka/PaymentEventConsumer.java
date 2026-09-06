package com.ayurveda.billing.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.common.kafka.KafkaTopics;
import com.ayurveda.common.kafka.PaymentEvent;
import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.common.tenant.TenantSchemaNames;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ayurveda.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PaymentEventConsumer {

    private final ObjectMapper objectMapper;
    private final PayuInvoicePaymentApplier payuInvoicePaymentApplier;

    @KafkaListener(topics = KafkaTopics.PAYMENTS, groupId = "billing-service")
    public void onPaymentEvent(String payload) {
        PaymentEvent event;
        try {
            event = objectMapper.readValue(payload, PaymentEvent.class);
        } catch (Exception ex) {
            log.warn("Ignoring unreadable payment event: {}", ex.getMessage());
            return;
        }

        if (event.getInvoiceId() == null || !"SUCCESS".equalsIgnoreCase(event.getStatus())) {
            return;
        }
        if (!TenantSchemaNames.isHospitalSchema(event.getSchemaName())
                || event.getTenantCode() == null
                || event.getTenantCode().isBlank()) {
            log.warn("Skipping payment event without hospital context. txnid={}", event.getPayuTxnId());
            return;
        }

        TenantContext.set(event.getTenantId(), event.getTenantCode(), event.getSchemaName());
        try {
            payuInvoicePaymentApplier.apply(event);
        } catch (ResourceNotFoundException | BadRequestException ex) {
            log.warn("Did not apply PayU payment to invoice {}: {}", event.getInvoiceId(), ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("Failed applying PayU payment to invoice {}: {}", event.getInvoiceId(), ex.getMessage(), ex);
            throw ex;
        } finally {
            TenantContext.clear();
        }
    }
}
