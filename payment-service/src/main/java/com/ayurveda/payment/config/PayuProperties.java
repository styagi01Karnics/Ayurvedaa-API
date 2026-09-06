package com.ayurveda.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "payu")
public class PayuProperties {

    private String merchantKey = "";
    private String merchantSalt = "";
    private String paymentUrl = "https://test.payu.in/_payment";
    private String successUrl = "http://localhost:8112/api/v1/payments/payu/success";
    private String failureUrl = "http://localhost:8112/api/v1/payments/payu/failure";

    public boolean isConfigured() {
        return merchantKey != null && !merchantKey.isBlank()
                && merchantSalt != null && !merchantSalt.isBlank();
    }
}
