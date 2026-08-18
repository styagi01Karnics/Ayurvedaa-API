package com.ayurveda.billing.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import com.ayurveda.billing.enums.InvoiceItemType;
import com.ayurveda.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "billing_invoice_items")
public class InvoiceItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InvoiceItemType itemType;

    @Column(nullable = false, length = 150)
    private String itemName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    private UUID medicineId;

    private UUID assignedTherapistId;

    @Column(length = 150)
    private String assignedTherapistName;

    private LocalDate scheduleDate;

    private LocalTime scheduleTime;

    private Integer sessionDuration;

    private Integer sessionFrequency;

}
