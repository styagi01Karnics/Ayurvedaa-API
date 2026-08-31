package com.ayurveda.fileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileUploadServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(FileUploadServiceApplication.class);
        app.setAdditionalProfiles("tenant");
        app.run(args);
    }

}
