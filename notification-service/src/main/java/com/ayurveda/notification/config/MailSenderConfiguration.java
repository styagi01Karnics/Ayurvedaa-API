package com.ayurveda.notification.config;

import java.util.Properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import lombok.extern.slf4j.Slf4j;

/**
 * Builds a single {@link JavaMailSender} from Gmail, Microsoft, or generic SPRING_MAIL_* env.
 * No bean is created when credentials are missing, so the service still starts locally.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(NotificationMailProperties.class)
public class MailSenderConfiguration {

    @Bean
    @Conditional(SmtpCredentialsPresentCondition.class)
    @ConditionalOnMissingBean(JavaMailSender.class)
    public JavaMailSender javaMailSender(NotificationMailProperties properties) {
        NotificationMailProperties.SmtpAccount account = properties.selectedAccount();
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(account.getHost().trim());
        sender.setPort(account.getPort());
        sender.setUsername(account.getUsername().trim());
        sender.setPassword(account.getPassword());

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", Boolean.toString(account.isAuth()));
        if (account.getPort() == 465) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.starttls.enable", "false");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        } else {
            props.put("mail.smtp.starttls.enable", Boolean.toString(account.isStarttls()));
            props.put("mail.smtp.starttls.required", Boolean.toString(account.isStarttls()));
        }
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        log.info("SMTP JavaMailSender ready provider={} host={} port={} user={}",
                properties.effectiveProvider(),
                sender.getHost(),
                sender.getPort(),
                sender.getUsername());
        return sender;
    }
}
