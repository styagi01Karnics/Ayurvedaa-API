package com.ayurveda.medicine.util;

import com.ayurveda.medicine.enums.MedicineStockStatus;

public final class MedicineStatusResolver {

    private MedicineStatusResolver() {
    }

    public static MedicineStockStatus resolve(int quantity, int lowStockThreshold) {
        if (quantity <= 0) {
            return MedicineStockStatus.OUT_OF_STOCK;
        }
        if (quantity <= lowStockThreshold) {
            return MedicineStockStatus.LOW_STOCK;
        }
        return MedicineStockStatus.IN_STOCK;
    }

}
