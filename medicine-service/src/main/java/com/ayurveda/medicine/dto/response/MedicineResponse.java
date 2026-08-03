package com.ayurveda.medicine.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.ayurveda.medicine.enums.MedicineCategory;
import com.ayurveda.medicine.enums.MedicineStatus;
import com.ayurveda.medicine.enums.MedicineStockStatus;

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
public class MedicineResponse {

    private UUID id;
    private String medicineName;
    private MedicineCategory category;
    private String manufacturer;
    private String batchNumber;
    private Integer stockQuantity;

    /** Same value as stockQuantity — kept for clients that send/update using quantity. */
    public Integer getQuantity() {
        return stockQuantity;
    }
    private LocalDate expiryDate;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal price;
    private Boolean lowStockAlertEnabled;
    private Integer lowStockThreshold;
    private MedicineStatus status;
    private MedicineStockStatus stockStatus;

}
