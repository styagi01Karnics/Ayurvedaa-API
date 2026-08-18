package com.ayurveda.billing.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ayurveda.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "billing_invoice_payments")
public class InvoicePayment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(nullable = false)
    private LocalDateTime paymentDate;

    @Column(length = 50)
    private String paymentMethod;

    @Column(length = 255)
    private String remarks;

}
