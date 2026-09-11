package com.ayurveda.notification.mail;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Builds a throwaway SMTP sender for a hospital Gmail or Microsoft mailbox.
 */
public final class HospitalMailSenderFactory {

    private HospitalMailSenderFactory() {
    }

    public static JavaMailSender create(TenantMailSettingsLookup.HospitalSmtpAccount account) {
        boolean gmail = account.provider() != null
                && "GMAIL".equalsIgnoreCase(account.provider());
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(gmail ? "smtp.gmail.com" : "smtp.office365.com");
        sender.setPort(587);
        sender.setUsername(account.fromEmail().trim());
        sender.setPassword(account.smtpPassword());
        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");
        return sender;
    }
}
