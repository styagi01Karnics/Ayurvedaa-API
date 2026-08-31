package com.ayurveda.doctor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DoctorServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DoctorServiceApplication.class);
        app.setAdditionalProfiles("tenant");
        app.run(args);
    }

}
