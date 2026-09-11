package com.ayurveda.auth.entity;

import com.ayurveda.auth.enums.PaymentGatewayMode;
import com.ayurveda.auth.enums.PaymentGatewayProvider;
import com.ayurveda.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tenant_payment_gateways")
public class TenantPaymentGateway extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String tenantCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentGatewayProvider provider;

    @Column(nullable = false, length = 100)
    private String merchantKey;

    @Column(nullable = false, length = 255)
    private String merchantSalt;

    @Column(length = 255)
    private String clientId;

    @Column(length = 255)
    private String clientSecret;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PaymentGatewayMode mode;

    @Column(length = 500)
    private String paymentUrl;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

}
