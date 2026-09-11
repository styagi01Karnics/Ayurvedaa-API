package com.ayurveda.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Single-tenant PayU settings from application.yml / env.
 * Do not commit live merchant key or salt — set them on the server.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "payu")
public class PayuProperties {

    private String merchantKey = "";
    private String merchantSalt = "";
    /** Optional OAuth client id (unused for hosted checkout hash). */
    private String clientId = "";
    /** Optional OAuth client secret. */
    private String clientSecret = "";
    /** TEST or LIVE. If blank, inferred from payment-url. */
    private String mode = "TEST";
    private String paymentUrl = "https://test.payu.in/_payment";
    /**
     * PayU merchant postservice (refund / verify). Defaults from mode when blank:
     * TEST → test.payu.in, LIVE → info.payu.in
     */
    private String postserviceUrl = "";
    private String successUrl = "http://45.195.229.15:8112/api/v1/payments/payu/success";
    private String failureUrl = "http://45.195.229.15:8112/api/v1/payments/payu/failure";

    public boolean isConfigured() {
        return merchantKey != null && !merchantKey.isBlank()
                && merchantSalt != null && !merchantSalt.isBlank();
    }

    public String resolvedPostserviceUrl() {
        if (postserviceUrl != null && !postserviceUrl.isBlank()) {
            return postserviceUrl.trim();
        }
        boolean live = mode != null && mode.trim().equalsIgnoreCase("LIVE");
        if (!live && paymentUrl != null && paymentUrl.toLowerCase().contains("secure.payu.in")) {
            live = true;
        }
        return live
                ? "https://info.payu.in/merchant/postservice.php?form=2"
                : "https://test.payu.in/merchant/postservice.php?form=2";
    }
}
