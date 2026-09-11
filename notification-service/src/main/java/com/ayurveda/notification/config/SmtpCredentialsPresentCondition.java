package com.ayurveda.notification.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * True when the selected SMTP account has host + username + password.
 */
public class SmtpCredentialsPresentCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        if (!env.getProperty("notification.mail.enabled", Boolean.class, Boolean.TRUE)) {
            return false;
        }
        if (ready(env, "notification.mail.host", "notification.mail.username", "notification.mail.password")
                || ready(env, "spring.mail.host", "spring.mail.username", "spring.mail.password")) {
            return true;
        }
        String provider = env.getProperty("notification.mail.provider", "gmail");
        if ("microsoft".equalsIgnoreCase(provider)) {
            return ready(env,
                    "notification.mail.microsoft.host",
                    "notification.mail.microsoft.username",
                    "notification.mail.microsoft.password");
        }
        return ready(env,
                "notification.mail.gmail.host",
                "notification.mail.gmail.username",
                "notification.mail.gmail.password");
    }

    private static boolean ready(Environment env, String hostKey, String userKey, String passKey) {
        return StringUtils.hasText(env.getProperty(hostKey))
                && StringUtils.hasText(env.getProperty(userKey))
                && StringUtils.hasText(env.getProperty(passKey));
    }
}
