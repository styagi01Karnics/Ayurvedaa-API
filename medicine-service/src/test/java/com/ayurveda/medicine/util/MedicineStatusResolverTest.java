package com.ayurveda.medicine.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ayurveda.medicine.enums.MedicineStockStatus;

class MedicineStatusResolverTest {

    @Test
    void resolve_outOfStockWhenQuantityZeroOrNegative() {
        assertEquals(MedicineStockStatus.OUT_OF_STOCK, MedicineStatusResolver.resolve(0, 20));
        assertEquals(MedicineStockStatus.OUT_OF_STOCK, MedicineStatusResolver.resolve(-1, 20));
    }

    @Test
    void resolve_lowStockWhenAtOrBelowThreshold() {
        assertEquals(MedicineStockStatus.LOW_STOCK, MedicineStatusResolver.resolve(1, 20));
        assertEquals(MedicineStockStatus.LOW_STOCK, MedicineStatusResolver.resolve(20, 20));
    }

    @Test
    void resolve_inStockWhenAboveThreshold() {
        assertEquals(MedicineStockStatus.IN_STOCK, MedicineStatusResolver.resolve(21, 20));
    }

}
