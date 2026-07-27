package com.ayurveda.medicine.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.medicine.constant.MedicineMessages;
import com.ayurveda.medicine.dto.request.CreateMedicineRequest;
import com.ayurveda.medicine.dto.request.StockAdjustRequest;
import com.ayurveda.medicine.dto.request.UpdateMedicineRequest;
import com.ayurveda.medicine.dto.response.CategoryStockCountResponse;
import com.ayurveda.medicine.dto.response.DashboardMedicineStockResponse;
import com.ayurveda.medicine.dto.response.MedicineResponse;
import com.ayurveda.medicine.dto.response.StockStatusBreakdownResponse;
import com.ayurveda.medicine.dto.response.StockSummaryResponse;
import com.ayurveda.medicine.entity.Medicine;
import com.ayurveda.medicine.enums.MedicineCategory;
import com.ayurveda.medicine.enums.MedicineStockStatus;
import com.ayurveda.medicine.mapper.MedicineMapper;
import com.ayurveda.medicine.repository.MedicineRepository;
import com.ayurveda.medicine.service.MedicineService;
import com.ayurveda.medicine.util.MedicineStatusResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MedicineServiceImpl implements MedicineService {

    private final MedicineRepository medicineRepository;
    private final MedicineMapper medicineMapper;

    @Value("${medicine.low-stock-threshold-default:20}")
    private int defaultLowStockThreshold;

    @Override
    public ApiResponse<MedicineResponse> createMedicine(CreateMedicineRequest request) {
        log.info("Creating medicine inventory for {}", request.getMedicineName());

        Medicine medicine = medicineMapper.toEntity(request, defaultLowStockThreshold);
        Medicine saved = medicineRepository.save(medicine);

        return ApiResponse.success(MedicineMessages.MEDICINE_ADDED_SUCCESSFULLY, medicineMapper.toResponse(saved));
    }

    @Override
    public ApiResponse<MedicineResponse> updateMedicine(UUID medicineId, UpdateMedicineRequest request) {
        Medicine medicine = findActive(medicineId);
        medicineMapper.updateEntity(medicine, request, defaultLowStockThreshold);
        Medicine saved = medicineRepository.save(medicine);

        return ApiResponse.success(
                MedicineMessages.MEDICINE_UPDATED_SUCCESSFULLY, medicineMapper.toResponse(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<MedicineResponse> getMedicineById(UUID medicineId) {
        return ApiResponse.success(medicineMapper.toResponse(findActive(medicineId)));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<MedicineResponse>> getMedicines(
            String medicineName, MedicineCategory category, MedicineStockStatus status) {

        String nameFilter = StringUtils.hasText(medicineName) ? medicineName.trim() : null;

        List<MedicineResponse> medicines = medicineRepository
                .search(nameFilter, category, status)
                .stream()
                .map(medicineMapper::toResponse)
                .toList();

        return ApiResponse.success(MedicineMessages.MEDICINES_FETCHED_SUCCESSFULLY, medicines);
    }

    @Override
    public ApiResponse<Void> deleteMedicine(UUID medicineId) {
        Medicine medicine = findActive(medicineId);
        medicine.setDeleted(true);
        medicineRepository.save(medicine);
        return ApiResponse.success(MedicineMessages.MEDICINE_DELETED_SUCCESSFULLY, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<MedicineCategory>> getCategories() {
        return ApiResponse.success(Arrays.asList(MedicineCategory.values()));
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<String>> getManufacturers() {
        return ApiResponse.success(medicineRepository.findDistinctManufacturers());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<String>> getMedicineNames() {
        return ApiResponse.success(medicineRepository.findDistinctMedicineNames());
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<StockSummaryResponse> getStockSummary() {
        List<CategoryStockCountResponse> byCategory = Arrays.stream(MedicineCategory.values())
                .map(category -> CategoryStockCountResponse.builder()
                        .category(category)
                        .totalStock(medicineRepository.sumStockByCategory(category))
                        .medicineCount(medicineRepository.countByCategory(category))
                        .build())
                .toList();

        StockSummaryResponse summary = StockSummaryResponse.builder()
                .totalStock(medicineRepository.sumTotalStock())
                .byCategory(byCategory)
                .build();

        return ApiResponse.success(MedicineMessages.STOCK_SUMMARY_FETCHED_SUCCESSFULLY, summary);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CategoryStockCountResponse> getStockCountByCategory(MedicineCategory category) {
        CategoryStockCountResponse response = CategoryStockCountResponse.builder()
                .category(category)
                .totalStock(medicineRepository.sumStockByCategory(category))
                .medicineCount(medicineRepository.countByCategory(category))
                .build();

        return ApiResponse.success(MedicineMessages.CATEGORY_STOCK_COUNT_FETCHED, response);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<MedicineResponse>> getLowStockMedicines() {
        List<MedicineResponse> medicines = medicineRepository
                .findByStatusAndDeletedFalseOrderByQuantityAsc(MedicineStockStatus.LOW_STOCK)
                .stream()
                .map(medicineMapper::toResponse)
                .toList();

        return ApiResponse.success(MedicineMessages.LOW_STOCK_MEDICINES_FETCHED, medicines);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<DashboardMedicineStockResponse> getDashboardMedicineStock(Integer lowStockLimit) {
        int limit = lowStockLimit != null && lowStockLimit > 0 ? lowStockLimit : 5;

        List<MedicineResponse> lowStockItems = medicineRepository
                .findByStatusAndDeletedFalseOrderByQuantityAsc(MedicineStockStatus.LOW_STOCK)
                .stream()
                .limit(limit)
                .map(medicineMapper::toResponse)
                .toList();

        DashboardMedicineStockResponse response = DashboardMedicineStockResponse.builder()
                .totalStock(medicineRepository.sumTotalStock())
                .tablets(medicineRepository.sumStockByCategory(MedicineCategory.TABLET))
                .syrups(medicineRepository.sumStockByCategory(MedicineCategory.SYRUP))
                .powder(medicineRepository.sumStockByCategory(MedicineCategory.POWDER))
                .statusBreakdown(StockStatusBreakdownResponse.builder()
                        .inStock(medicineRepository.countByStatusAndDeletedFalse(MedicineStockStatus.IN_STOCK))
                        .outOfStock(medicineRepository.countByStatusAndDeletedFalse(MedicineStockStatus.OUT_OF_STOCK))
                        .lowStock(medicineRepository.countByStatusAndDeletedFalse(MedicineStockStatus.LOW_STOCK))
                        .build())
                .lowStockItems(lowStockItems)
                .build();

        return ApiResponse.success(MedicineMessages.DASHBOARD_MEDICINE_STOCK_AVAILABILITY_FETCHED, response);
    }

    @Override
    public ApiResponse<MedicineResponse> deductStock(UUID medicineId, StockAdjustRequest request) {
        Medicine medicine = findActive(medicineId);
        int qty = request.getQuantity();

        if (medicine.getQuantity() < qty) {
            throw new BadRequestException(
                    MedicineMessages.INSUFFICIENT_STOCK_PREFIX + medicine.getMedicineName()
                            + "'. Available: " + medicine.getQuantity() + ", requested: " + qty);
        }

        medicine.setQuantity(medicine.getQuantity() - qty);
        medicine.setStatus(MedicineStatusResolver.resolve(
                medicine.getQuantity(), medicine.getLowStockThreshold()));
        Medicine saved = medicineRepository.save(medicine);

        return ApiResponse.success(MedicineMessages.STOCK_DEDUCTED_SUCCESSFULLY, medicineMapper.toResponse(saved));
    }

    @Override
    public ApiResponse<MedicineResponse> restoreStock(UUID medicineId, StockAdjustRequest request) {
        Medicine medicine = findActive(medicineId);
        medicine.setQuantity(medicine.getQuantity() + request.getQuantity());
        medicine.setStatus(MedicineStatusResolver.resolve(
                medicine.getQuantity(), medicine.getLowStockThreshold()));
        Medicine saved = medicineRepository.save(medicine);

        return ApiResponse.success(MedicineMessages.STOCK_RESTORED_SUCCESSFULLY, medicineMapper.toResponse(saved));
    }

    private Medicine findActive(UUID medicineId) {
        return medicineRepository.findByIdAndDeletedFalse(medicineId)
                .orElseThrow(() -> new ResourceNotFoundException(MedicineMessages.MEDICINE_NOT_FOUND));
    }

}
