package com.ayurveda.medicine.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ayurveda.medicine.enums.MedicineCategory;
import com.ayurveda.medicine.enums.MedicineStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UpdateMedicineRequest {

    @NotBlank(message = "Medicine name is required")
    @Size(max = 150)
    private String medicineName;

    @NotNull(message = "Medicine category is required")
    private MedicineCategory category;

    @NotBlank(message = "Manufacturer is required")
    @Size(max = 150)
    private String manufacturer;

    @NotBlank(message = "Batch number is required")
    @Size(max = 100)
    private String batchNumber;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    @NotNull(message = "Purchase price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase price must be 0 or more")
    private BigDecimal purchasePrice;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Selling price must be 0 or more")
    private BigDecimal sellingPrice;

    private Boolean lowStockAlertEnabled;

    @Min(value = 1, message = "Low stock threshold must be at least 1")
    private Integer lowStockThreshold;

    private MedicineStatus status;

}
