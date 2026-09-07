package com.ayurveda.payment.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ayurveda.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "payment_links")
public class PaymentLink extends BaseEntity {

    @Column(nullable = false, unique = true, length = 120)
    private String token;

    @Column(nullable = false)
    private UUID invoiceId;

    private UUID patientId;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 80)
    private String invoiceNumber;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 63)
    private String schemaName;

    @Column(length = 50)
    private String tenantCode;

    private UUID tenantId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(length = 20)
    private String status;

    @PrePersist
    void prePersist() {
        if (getDeleted() == null) {
            setDeleted(false);
        }
        if (status == null) {
            status = "OPEN";
        }
    }
}
