package com.ayurveda.billing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients
@EnableKafka
public class BillingServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(BillingServiceApplication.class);
        app.setAdditionalProfiles("tenant");
        app.run(args);
    }

}
