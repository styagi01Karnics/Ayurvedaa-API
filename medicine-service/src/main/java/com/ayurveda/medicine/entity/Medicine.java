package com.ayurveda.medicine.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ayurveda.common.BaseEntity;
import com.ayurveda.medicine.enums.MedicineCategory;
import com.ayurveda.medicine.enums.MedicineStatus;
import com.ayurveda.medicine.enums.MedicineStockStatus;
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
@Table(name = "medicine_inventory")
public class Medicine extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String medicineName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MedicineCategory category;

    @Column(nullable = false, length = 150)
    private String manufacturer;

    @Column(nullable = false, length = 100)
    private String batchNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDate expiryDate;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Builder.Default
    @Column(nullable = false)
    private Boolean lowStockAlertEnabled = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer lowStockThreshold = 20;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedicineStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MedicineStockStatus stockStatus;

}
