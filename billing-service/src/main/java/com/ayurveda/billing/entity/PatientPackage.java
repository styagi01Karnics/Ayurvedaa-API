package com.ayurveda.billing.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.ayurveda.billing.enums.PackageStatus;
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
@Table(name = "patient_packages")
public class PatientPackage extends BaseEntity {

    @Column(nullable = false)
    private UUID patientId;

    @Column(name = "package_master_id", nullable = false)
    private UUID packageMasterId;

    @Column(nullable = false)
    private LocalDate validity;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 30)
    private PackageStatus status = PackageStatus.SCHEDULED;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal discountApplied;

}
