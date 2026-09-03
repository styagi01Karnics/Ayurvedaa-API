package com.ayurveda.billing.dto.client;

import java.math.BigDecimal;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicineClientResponse {

    private UUID id;
    private String medicineName;
    private Integer stockQuantity;
    private BigDecimal sellingPrice;
    private BigDecimal price;
    private String status;

}
