package com.ayurveda.auth.dto.response;

import java.time.LocalDateTime;

import com.ayurveda.auth.enums.PaymentGatewayMode;
import com.ayurveda.auth.enums.PaymentGatewayProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Super Admin view of a hospital PayU config. Salt and client secret are masked.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantPaymentGatewayResponse {

    private String tenantCode;
    private PaymentGatewayProvider provider;
    private String merchantKey;
    private String merchantSaltMasked;
    private String clientId;
    private String clientSecretMasked;
    private PaymentGatewayMode mode;
    private String paymentUrl;
    private String resolvedPaymentUrl;
    private Boolean enabled;
    private LocalDateTime updatedAt;

}
