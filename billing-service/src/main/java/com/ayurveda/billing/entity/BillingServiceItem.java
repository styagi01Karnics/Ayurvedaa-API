package com.ayurveda.billing.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.ayurveda.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "billing_service_items")
public class BillingServiceItem extends BaseEntity {

    @Column(nullable = false)
    private UUID billingId;

    @Column(length = 100)
    private String serviceType;

    @Column(precision = 12, scale = 2)
    private BigDecimal serviceFees;

    /** FK to mst_package (optional). */
    private UUID packageMasterId;

    @Column(length = 100)
    private String packageType;

    @Column(precision = 12, scale = 2)
    private BigDecimal packageCharges;

}
