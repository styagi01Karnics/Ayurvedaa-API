package com.ayurveda.auth.dto.request;

import com.ayurveda.auth.enums.PaymentGatewayMode;
import com.ayurveda.auth.enums.PaymentGatewayProvider;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Super Admin per-tenant PayU credentials. Salt/secret are stored encrypted-at-rest
 * only by Postgres access control; they are never returned in the clear.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertTenantPaymentGatewayRequest {

    /** Defaults to PAYU when omitted. */
    private PaymentGatewayProvider provider;

    @NotBlank(message = "Merchant key is required")
    @Size(max = 100)
    private String merchantKey;

    /** Required on first save; omit or blank on update to keep the existing salt. */
    @Size(max = 255)
    private String merchantSalt;

    @Size(max = 255)
    private String clientId;

    /** Omit or blank on update to keep the existing client secret. */
    @Size(max = 255)
    private String clientSecret;

    @NotNull(message = "Mode is required")
    private PaymentGatewayMode mode;

    @Size(max = 500)
    private String paymentUrl;

    /** Defaults to true when omitted. */
    private Boolean enabled;

}
