package com.ayurveda.patient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PatientServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(PatientServiceApplication.class);
        app.setAdditionalProfiles("tenant");
        app.run(args);
    }

}
