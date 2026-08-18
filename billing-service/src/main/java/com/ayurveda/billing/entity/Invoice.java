package com.ayurveda.billing.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ayurveda.billing.enums.InvoiceStatus;
import com.ayurveda.billing.enums.VisitType;
import com.ayurveda.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
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
@Table(name = "billing_invoices")
public class Invoice extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String invoiceNumber;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false, length = 150)
    private String patientName;

    @Column(length = 20)
    private String contactNumber;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private VisitType visitType;

    @Column(precision = 12, scale = 2)
    private BigDecimal serviceFees;

    /** FK to mst_package (optional) — same as billing service items. */
    private UUID packageMasterId;

    @Column(length = 100)
    private String packageType;

    @Column(precision = 12, scale = 2)
    private BigDecimal packageCharges;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false)
    private Boolean taxEnabled = false;

    @Column(precision = 5, scale = 2)
    private BigDecimal cgstPercent;

    @Column(precision = 12, scale = 2)
    private BigDecimal cgstAmount;

    @Column(precision = 5, scale = 2)
    private BigDecimal sgstPercent;

    @Column(precision = 12, scale = 2)
    private BigDecimal sgstAmount;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal leftAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvoiceStatus status;

    /**
     * Comma-separated sections included in this bill, e.g. SERVICE,MEDICINE or SERVICE,THERAPY or ALL sections.
     */
    @Column(nullable = false, length = 50)
    private String billSections;

    @Builder.Default
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoiceItem> items = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<InvoicePayment> payments = new ArrayList<>();

}
