package com.ayurveda.therapist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TherapistServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TherapistServiceApplication.class, args);
    }

}
