package com.ayurveda.medicine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MedicineServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MedicineServiceApplication.class);
        app.setAdditionalProfiles("tenant");
        app.run(args);
    }

}
