package com.ayurveda.notification.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
@ConditionalOnProperty(prefix = "firebase.push", name = "enabled", havingValue = "true")
public class FirebasePushConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging(
            @Value("${firebase.push.service-account-path:}") String serviceAccountPath) throws IOException {

        if (serviceAccountPath.isBlank()) {
            throw new IllegalStateException(
                    "FIREBASE_SERVICE_ACCOUNT_PATH must be set when firebase.push.enabled=true");
        }
        FirebaseApp app;
        try (InputStream credentials = new FileInputStream(serviceAccountPath)) {
            app = FirebaseApp.getApps().isEmpty()
                    ? FirebaseApp.initializeApp(FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(credentials))
                            .build())
                    : FirebaseApp.getInstance();
        }
        return FirebaseMessaging.getInstance(app);
    }
}
