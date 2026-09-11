package com.ayurveda.payment.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.ayurveda.common.BaseEntity;
import com.ayurveda.payment.enums.PaymentGateway;
import com.ayurveda.payment.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
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
@Table(name = "payments")
public class PaymentTransaction extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String paymentCode;

    @Column(nullable = false, unique = true, length = 25)
    private String payuTxnId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 100)
    private String productInfo;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    private UUID invoiceId;

    private UUID patientId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentGateway gateway = PaymentGateway.PAYU;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.INITIATED;

    @Column(length = 50)
    private String payuStatus;

    @Column(length = 100)
    private String mihpayid;

    @Column(length = 50)
    private String paymentMode;

    @Column(length = 500)
    private String errorMessage;

    @Column(length = 500)
    private String requestHash;

    @Column(length = 63)
    private String schemaName;

    @Column(length = 50)
    private String tenantCode;

    @Column(length = 500)
    private String frontendSuccessUrl;

    @Column(length = 500)
    private String frontendFailureUrl;

    private UUID initiatedByUserId;

    @Column(columnDefinition = "TEXT")
    private String rawCallback;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal refundedAmount = BigDecimal.ZERO;

    @Column(length = 100)
    private String lastRefundRequestId;

    @Column(length = 64)
    private String lastRefundToken;

    @PrePersist
    void prePersist() {
        if (getDeleted() == null) {
            setDeleted(false);
        }
        if (gateway == null) {
            gateway = PaymentGateway.PAYU;
        }
        if (status == null) {
            status = PaymentStatus.INITIATED;
        }
        if (refundedAmount == null) {
            refundedAmount = BigDecimal.ZERO;
        }
    }
}
