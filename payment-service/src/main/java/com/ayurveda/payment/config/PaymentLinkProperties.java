package com.ayurveda.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "ayurveda.payment")
public class PaymentLinkProperties {

    /** Public UI origin used to build /pay/{token}. */
    private String payPageBaseUrl = "http://localhost:5173";
}
