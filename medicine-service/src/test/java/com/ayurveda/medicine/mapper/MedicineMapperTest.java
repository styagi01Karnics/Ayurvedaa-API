package com.ayurveda.medicine.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayurveda.medicine.dto.request.CreateMedicineRequest;
import com.ayurveda.medicine.dto.request.UpdateMedicineRequest;
import com.ayurveda.medicine.dto.response.MedicineResponse;
import com.ayurveda.medicine.entity.Medicine;
import com.ayurveda.medicine.enums.MedicineCategory;
import com.ayurveda.medicine.enums.MedicineStatus;
import com.ayurveda.medicine.enums.MedicineStockStatus;

class MedicineMapperTest {

    private MedicineMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MedicineMapper();
    }

    @Test
    void toEntity_appliesDefaultsAndStockStatus() {
        CreateMedicineRequest request = CreateMedicineRequest.builder()
                .medicineName(" Ashwagandha ")
                .category(MedicineCategory.POWDER)
                .manufacturer(" Ayur Co ")
                .batchNumber(" B1 ")
                .quantity(5)
                .expiryDate(LocalDate.of(2027, 1, 1))
                .purchasePrice(BigDecimal.TEN)
                .sellingPrice(BigDecimal.valueOf(15))
                .lowStockAlertEnabled(true)
                .build();

        Medicine medicine = mapper.toEntity(request, 20);

        assertEquals("Ashwagandha", medicine.getMedicineName());
        assertEquals("Ayur Co", medicine.getManufacturer());
        assertEquals("B1", medicine.getBatchNumber());
        assertEquals(20, medicine.getLowStockThreshold());
        assertEquals(MedicineStatus.ACTIVE, medicine.getStatus());
        assertEquals(MedicineStockStatus.LOW_STOCK, medicine.getStockStatus());
        assertTrue(medicine.getLowStockAlertEnabled());
    }

    @Test
    void updateEntity_updatesFieldsAndStatus() {
        Medicine medicine = Medicine.builder()
                .medicineName("Old")
                .category(MedicineCategory.TABLET)
                .manufacturer("OldCo")
                .batchNumber("OLD")
                .quantity(100)
                .lowStockThreshold(10)
                .status(MedicineStatus.ACTIVE)
                .stockStatus(MedicineStockStatus.IN_STOCK)
                .build();

        UpdateMedicineRequest request = UpdateMedicineRequest.builder()
                .medicineName("New")
                .category(MedicineCategory.SYRUP)
                .manufacturer("NewCo")
                .batchNumber("NEW")
                .quantity(0)
                .expiryDate(LocalDate.of(2028, 1, 1))
                .purchasePrice(BigDecimal.ONE)
                .sellingPrice(BigDecimal.TWO)
                .lowStockAlertEnabled(false)
                .status(MedicineStatus.INACTIVE)
                .build();

        mapper.updateEntity(medicine, request, 20);

        assertEquals("New", medicine.getMedicineName());
        assertEquals(MedicineCategory.SYRUP, medicine.getCategory());
        assertEquals(MedicineStockStatus.OUT_OF_STOCK, medicine.getStockStatus());
        assertEquals(MedicineStatus.INACTIVE, medicine.getStatus());
        assertFalse(medicine.getLowStockAlertEnabled());
    }

    @Test
    void toResponse_mapsCoreFields() {
        Medicine medicine = Medicine.builder()
                .medicineName("Triphala")
                .category(MedicineCategory.TABLET)
                .manufacturer("Herb")
                .batchNumber("T1")
                .quantity(50)
                .sellingPrice(BigDecimal.valueOf(99))
                .status(MedicineStatus.ACTIVE)
                .stockStatus(MedicineStockStatus.IN_STOCK)
                .build();

        MedicineResponse response = mapper.toResponse(medicine);

        assertEquals("Triphala", response.getMedicineName());
        assertEquals(50, response.getStockQuantity());
        assertEquals(BigDecimal.valueOf(99), response.getPrice());
    }

}
