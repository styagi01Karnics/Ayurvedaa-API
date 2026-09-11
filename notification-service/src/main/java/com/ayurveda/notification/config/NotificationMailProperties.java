package com.ayurveda.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import com.ayurveda.notification.enums.MailProvider;

import lombok.Getter;
import lombok.Setter;

/**
 * SMTP settings for notification-service. Passwords come from env, never from git.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "notification.mail")
public class NotificationMailProperties {

    /** When false, emails are logged and not sent. */
    private boolean enabled = true;

    /** {@code gmail} or {@code microsoft}. Ignored when generic {@code host} is set. */
    private MailProvider provider = MailProvider.GMAIL;

    /** From-header. Defaults to the selected account username when blank. */
    private String from;

    /**
     * Generic override ({@code SPRING_MAIL_HOST}). When host+username+password are set,
     * this account is used instead of the Gmail/Microsoft presets.
     */
    private String host;
    private int port = 587;
    private String username;
    private String password;
    private boolean starttls = true;
    private boolean auth = true;

    private SmtpAccount gmail = new SmtpAccount();
    private SmtpAccount microsoft = new SmtpAccount();

    public SmtpAccount selectedAccount() {
        if (hasCredentials(host, username, password)) {
            SmtpAccount generic = new SmtpAccount();
            generic.setHost(host);
            generic.setPort(port);
            generic.setUsername(username);
            generic.setPassword(password);
            generic.setStarttls(starttls);
            generic.setAuth(auth);
            return generic;
        }
        if (provider == MailProvider.MICROSOFT) {
            return microsoft != null ? microsoft : new SmtpAccount();
        }
        return gmail != null ? gmail : new SmtpAccount();
    }

    public MailProvider effectiveProvider() {
        if (hasCredentials(host, username, password)) {
            return provider != null ? provider : MailProvider.GMAIL;
        }
        return provider != null ? provider : MailProvider.GMAIL;
    }

    public String resolveFrom() {
        if (StringUtils.hasText(from)) {
            return from.trim();
        }
        SmtpAccount account = selectedAccount();
        return account != null ? trimToNull(account.getUsername()) : null;
    }

    public boolean smtpReady() {
        return enabled && selectedAccount().hasCredentials();
    }

    static boolean hasCredentials(String host, String username, String password) {
        return StringUtils.hasText(host) && StringUtils.hasText(username) && StringUtils.hasText(password);
    }

    private static String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    @Getter
    @Setter
    public static class SmtpAccount {
        private String host;
        private int port = 587;
        private String username;
        private String password;
        private boolean starttls = true;
        private boolean auth = true;

        public boolean hasCredentials() {
            return NotificationMailProperties.hasCredentials(host, username, password);
        }
    }
}
