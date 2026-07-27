package com.ayurveda.medicine.dto.response;

import com.ayurveda.medicine.enums.MedicineCategory;

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
public class CategoryStockCountResponse {

    private MedicineCategory category;
    private long totalStock;
    private long medicineCount;

}
