package com.ayurveda.medicine.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.medicine.dto.request.CreateMedicineRequest;
import com.ayurveda.medicine.dto.request.StockAdjustRequest;
import com.ayurveda.medicine.dto.request.UpdateMedicineRequest;
import com.ayurveda.medicine.dto.response.MedicineNameResponse;
import com.ayurveda.medicine.dto.response.MedicineResponse;
import com.ayurveda.medicine.entity.Medicine;
import com.ayurveda.medicine.enums.MedicineCategory;
import com.ayurveda.medicine.enums.MedicineStatus;
import com.ayurveda.medicine.enums.MedicineStockStatus;
import com.ayurveda.medicine.mapper.MedicineMapper;
import com.ayurveda.medicine.repository.MedicineRepository;

@ExtendWith(MockitoExtension.class)
class MedicineServiceImplTest {

    @Mock
    private MedicineRepository medicineRepository;

    @Mock
    private MedicineMapper medicineMapper;

    @InjectMocks
    private MedicineServiceImpl medicineService;

    private UUID medicineId;
    private Medicine medicine;
    private MedicineResponse medicineResponse;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(medicineService, "defaultLowStockThreshold", 20);
        medicineId = UUID.randomUUID();
        medicine = Medicine.builder()
                .medicineName("Ashwagandha")
                .category(MedicineCategory.POWDER)
                .quantity(50)
                .lowStockThreshold(20)
                .status(MedicineStatus.ACTIVE)
                .stockStatus(MedicineStockStatus.IN_STOCK)
                .build();
        ReflectionTestUtils.setField(medicine, "id", medicineId);
        medicineResponse = MedicineResponse.builder()
                .id(medicineId)
                .medicineName("Ashwagandha")
                .stockQuantity(50)
                .build();
    }

    @Test
    void createMedicines_savesAndReturnsResponses() {
        CreateMedicineRequest request = CreateMedicineRequest.builder()
                .medicineName("Ashwagandha")
                .category(MedicineCategory.POWDER)
                .manufacturer("Ayur")
                .batchNumber("B1")
                .quantity(50)
                .expiryDate(LocalDate.now().plusYears(1))
                .purchasePrice(BigDecimal.TEN)
                .sellingPrice(BigDecimal.TEN)
                .build();

        when(medicineMapper.toEntity(request, 20)).thenReturn(medicine);
        when(medicineRepository.saveAll(anyList())).thenReturn(List.of(medicine));
        when(medicineMapper.toResponse(medicine)).thenReturn(medicineResponse);

        ApiResponse<List<MedicineResponse>> response = medicineService.createMedicines(List.of(request));

        assertTrue(response.isSuccess());
        assertEquals(1, response.getData().size());
        assertEquals(medicineId, response.getData().get(0).getId());
    }

    @Test
    void getMedicineById_notFound_throws() {
        when(medicineRepository.findByIdAndDeletedFalse(medicineId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> medicineService.getMedicineById(medicineId));
    }

    @Test
    void getMedicineNames_returnsIdAndName() {
        when(medicineRepository.findAllNamesOrdered()).thenReturn(List.of(medicine));

        ApiResponse<List<MedicineNameResponse>> response = medicineService.getMedicineNames();

        assertEquals(1, response.getData().size());
        assertEquals(medicineId, response.getData().get(0).getId());
        assertEquals("Ashwagandha", response.getData().get(0).getMedicineName());
    }

    @Test
    void deleteMedicine_softDeletesAndInactivates() {
        when(medicineRepository.findByIdAndDeletedFalse(medicineId)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(medicine)).thenReturn(medicine);

        ApiResponse<Void> response = medicineService.deleteMedicine(medicineId);

        assertTrue(response.isSuccess());
        assertEquals(Boolean.TRUE, medicine.getDeleted());
        assertEquals(MedicineStatus.INACTIVE, medicine.getStatus());
        verify(medicineRepository).save(medicine);
    }

    @Test
    void deductStock_insufficient_throws() {
        medicine.setQuantity(2);
        when(medicineRepository.findByIdAndDeletedFalse(medicineId)).thenReturn(Optional.of(medicine));

        StockAdjustRequest request = new StockAdjustRequest();
        request.setQuantity(5);

        assertThrows(BadRequestException.class, () -> medicineService.deductStock(medicineId, request));
    }

    @Test
    void deductStock_reducesQuantity() {
        when(medicineRepository.findByIdAndDeletedFalse(medicineId)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenAnswer(inv -> inv.getArgument(0));
        when(medicineMapper.toResponse(any(Medicine.class))).thenReturn(medicineResponse);

        StockAdjustRequest request = new StockAdjustRequest();
        request.setQuantity(10);

        ApiResponse<MedicineResponse> response = medicineService.deductStock(medicineId, request);

        assertTrue(response.isSuccess());
        assertEquals(40, medicine.getQuantity());
        assertEquals(MedicineStockStatus.IN_STOCK, medicine.getStockStatus());
    }

    @Test
    void restoreStock_increasesQuantity() {
        when(medicineRepository.findByIdAndDeletedFalse(medicineId)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenAnswer(inv -> inv.getArgument(0));
        when(medicineMapper.toResponse(any(Medicine.class))).thenReturn(medicineResponse);

        StockAdjustRequest request = new StockAdjustRequest();
        request.setQuantity(5);

        medicineService.restoreStock(medicineId, request);

        assertEquals(55, medicine.getQuantity());
    }

    @Test
    void updateMedicine_updatesExisting() {
        UpdateMedicineRequest request = UpdateMedicineRequest.builder()
                .medicineName("Updated")
                .category(MedicineCategory.TABLET)
                .manufacturer("M")
                .batchNumber("B")
                .quantity(10)
                .expiryDate(LocalDate.now().plusYears(1))
                .purchasePrice(BigDecimal.ONE)
                .sellingPrice(BigDecimal.ONE)
                .build();

        when(medicineRepository.findByIdAndDeletedFalse(medicineId)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(medicine)).thenReturn(medicine);
        when(medicineMapper.toResponse(medicine)).thenReturn(medicineResponse);

        ApiResponse<MedicineResponse> response = medicineService.updateMedicine(medicineId, request);

        assertTrue(response.isSuccess());
        verify(medicineMapper).updateEntity(medicine, request, 20);
    }

    @Test
    void getCategories_returnsAll() {
        assertEquals(MedicineCategory.values().length, medicineService.getCategories().getData().size());
    }

    @Test
    void getDashboardMedicineStock_limitsLowStockItems() {
        when(medicineRepository.findByStockStatusAndDeletedFalseOrderByQuantityAsc(MedicineStockStatus.LOW_STOCK))
                .thenReturn(List.of(medicine, medicine, medicine));
        when(medicineMapper.toResponse(medicine)).thenReturn(medicineResponse);
        when(medicineRepository.sumTotalStock()).thenReturn(100L);
        when(medicineRepository.sumStockByCategory(any())).thenReturn(10L);
        when(medicineRepository.countByStockStatusAndDeletedFalse(any())).thenReturn(1L);

        var response = medicineService.getDashboardMedicineStock(2);

        assertEquals(2, response.getData().getLowStockItems().size());
    }

}
