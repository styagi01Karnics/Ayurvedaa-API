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
    private final PayuInvoiceRefundApplier payuInvoiceRefundApplier;

    @KafkaListener(topics = KafkaTopics.PAYMENTS, groupId = "billing-service")
    public void onPaymentEvent(String payload) {
        PaymentEvent event;
        try {
            event = objectMapper.readValue(payload, PaymentEvent.class);
        } catch (Exception ex) {
            log.warn("Ignoring unreadable payment event: {}", ex.getMessage());
            return;
        }

        if (event.getInvoiceId() == null || event.getStatus() == null) {
            return;
        }

        String status = event.getStatus().trim().toUpperCase();
        boolean isSuccess = "SUCCESS".equals(status);
        boolean isRefund = "REFUNDED".equals(status) || "PARTIALLY_REFUNDED".equals(status);
        if (!isSuccess && !isRefund) {
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
            if (isRefund) {
                payuInvoiceRefundApplier.apply(event);
            } else {
                payuInvoicePaymentApplier.apply(event);
            }
        } catch (ResourceNotFoundException | BadRequestException ex) {
            log.warn("Did not apply payment event to invoice {}: {}", event.getInvoiceId(), ex.getMessage());
        } catch (RuntimeException ex) {
            log.error("Failed applying payment event to invoice {}: {}", event.getInvoiceId(), ex.getMessage(), ex);
            throw ex;
        } finally {
            TenantContext.clear();
        }
    }
}
