package com.ayurveda.billing.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.ayurveda.billing.enums.BillingStatus;
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

/**
 * Doctor billing draft (section 3) — services only. No discount/GST/medicine/therapy.
 * Invoice is created separately; medicines are added on the invoice.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "billings")
public class Billing extends BaseEntity {

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false, length = 150)
    private String patientName;

    @Column(length = 20)
    private String contactNumber;

    @Column(nullable = false)
    private LocalDate billingDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private BillingStatus status = BillingStatus.PENDING;

    private UUID invoiceId;

    @Column(length = 50)
    private String invoiceNumber;

}
