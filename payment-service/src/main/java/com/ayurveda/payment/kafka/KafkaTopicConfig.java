package com.ayurveda.payment.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import com.ayurveda.common.kafka.KafkaTopics;

@Configuration
@ConditionalOnProperty(prefix = "ayurveda.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaTopicConfig {

    @Bean
    public NewTopic paymentsTopic() {
        return TopicBuilder.name(KafkaTopics.PAYMENTS)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
