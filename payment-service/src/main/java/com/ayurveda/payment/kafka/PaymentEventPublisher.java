package com.ayurveda.payment.kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ayurveda.common.kafka.KafkaTopics;
import com.ayurveda.common.kafka.PaymentEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ayurveda.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PaymentEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(PaymentEvent event) {
        if (event == null || event.getPayuTxnId() == null) {
            return;
        }
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(KafkaTopics.PAYMENTS, event.getPayuTxnId(), payload)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.warn("Kafka publish failed for txnid={}: {}", event.getPayuTxnId(), ex.getMessage());
                        } else {
                            log.info(
                                    "Published {} to {} for txnid={}",
                                    event.getEventType(),
                                    KafkaTopics.PAYMENTS,
                                    event.getPayuTxnId());
                        }
                    });
        } catch (JsonProcessingException ex) {
            log.warn("Failed to serialize payment event for txnid={}: {}", event.getPayuTxnId(), ex.getMessage());
        } catch (RuntimeException ex) {
            log.warn("Kafka publish failed for txnid={}: {}", event.getPayuTxnId(), ex.getMessage());
        }
    }
}
