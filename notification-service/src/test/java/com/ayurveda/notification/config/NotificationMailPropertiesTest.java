package com.ayurveda.notification.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ayurveda.notification.enums.MailProvider;

class NotificationMailPropertiesTest {

    @Test
    void selectsGmailAccountByDefault() {
        NotificationMailProperties props = new NotificationMailProperties();
        NotificationMailProperties.SmtpAccount gmail = new NotificationMailProperties.SmtpAccount();
        gmail.setHost("smtp.gmail.com");
        gmail.setUsername("a@gmail.com");
        gmail.setPassword("app-pass");
        props.setGmail(gmail);

        assertEquals("smtp.gmail.com", props.selectedAccount().getHost());
        assertEquals(MailProvider.GMAIL, props.effectiveProvider());
        assertTrue(props.smtpReady());
    }

    @Test
    void selectsMicrosoftWhenProviderSet() {
        NotificationMailProperties props = new NotificationMailProperties();
        props.setProvider(MailProvider.MICROSOFT);
        NotificationMailProperties.SmtpAccount ms = new NotificationMailProperties.SmtpAccount();
        ms.setHost("smtp.office365.com");
        ms.setUsername("a@outlook.com");
        ms.setPassword("secret");
        props.setMicrosoft(ms);

        assertEquals("smtp.office365.com", props.selectedAccount().getHost());
        assertEquals("a@outlook.com", props.resolveFrom());
        assertTrue(props.selectedAccount().hasCredentials());
    }

    @Test
    void genericSpringMailHostWinsOverProvider() {
        NotificationMailProperties props = new NotificationMailProperties();
        props.setProvider(MailProvider.MICROSOFT);
        props.setHost("smtp.example.com");
        props.setUsername("ops@example.com");
        props.setPassword("secret");
        NotificationMailProperties.SmtpAccount ms = new NotificationMailProperties.SmtpAccount();
        ms.setHost("smtp.office365.com");
        props.setMicrosoft(ms);

        assertEquals("smtp.example.com", props.selectedAccount().getHost());
        assertEquals("ops@example.com", props.selectedAccount().getUsername());
    }

    @Test
    void notReadyWithoutPassword() {
        NotificationMailProperties props = new NotificationMailProperties();
        NotificationMailProperties.SmtpAccount gmail = new NotificationMailProperties.SmtpAccount();
        gmail.setHost("smtp.gmail.com");
        gmail.setUsername("a@gmail.com");
        props.setGmail(gmail);
        assertFalse(props.smtpReady());
    }
}
