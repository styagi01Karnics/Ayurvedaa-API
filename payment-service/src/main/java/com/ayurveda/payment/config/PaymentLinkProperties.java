package com.ayurveda.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "ayurveda.payment")
public class PaymentLinkProperties {

    /** Public payment-service host used to build /pay/{token} and qrPayload (not localhost). */
    private String payPageBaseUrl = "http://45.195.229.15:8112";
}
