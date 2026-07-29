package com.ayurveda.medicine.service;

import java.util.List;
import java.util.UUID;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.medicine.dto.request.CreateMedicineRequest;
import com.ayurveda.medicine.dto.request.StockAdjustRequest;
import com.ayurveda.medicine.dto.request.UpdateMedicineRequest;
import com.ayurveda.medicine.dto.response.CategoryStockCountResponse;
import com.ayurveda.medicine.dto.response.DashboardMedicineStockResponse;
import com.ayurveda.medicine.dto.response.MedicineNameResponse;
import com.ayurveda.medicine.dto.response.MedicineResponse;
import com.ayurveda.medicine.dto.response.StockSummaryResponse;
import com.ayurveda.medicine.enums.MedicineCategory;
import com.ayurveda.medicine.enums.MedicineStockStatus;

public interface MedicineService {

    ApiResponse<List<MedicineResponse>> createMedicines(List<CreateMedicineRequest> requests);

    ApiResponse<MedicineResponse> updateMedicine(UUID medicineId, UpdateMedicineRequest request);

    ApiResponse<MedicineResponse> getMedicineById(UUID medicineId);

    ApiResponse<List<MedicineResponse>> getMedicines(
            String medicineName, MedicineCategory category, MedicineStockStatus stockStatus);

    ApiResponse<Void> deleteMedicine(UUID medicineId);

    ApiResponse<List<MedicineCategory>> getCategories();

    ApiResponse<List<String>> getManufacturers();

    ApiResponse<List<MedicineNameResponse>> getMedicineNames();

    ApiResponse<StockSummaryResponse> getStockSummary();

    ApiResponse<CategoryStockCountResponse> getStockCountByCategory(MedicineCategory category);

    ApiResponse<List<MedicineResponse>> getLowStockMedicines();

    /** Dashboard page – Medicine Stock Availability card. */
    ApiResponse<DashboardMedicineStockResponse> getDashboardMedicineStock(Integer lowStockLimit);

    ApiResponse<MedicineResponse> deductStock(UUID medicineId, StockAdjustRequest request);

    ApiResponse<MedicineResponse> restoreStock(UUID medicineId, StockAdjustRequest request);

}
