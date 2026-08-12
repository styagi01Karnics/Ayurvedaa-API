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

    /** Creates one or more medicine inventory records. */
    ApiResponse<List<MedicineResponse>> createMedicines(List<CreateMedicineRequest> requests);

    /** Updates an existing medicine record. */
    ApiResponse<MedicineResponse> updateMedicine(UUID medicineId, UpdateMedicineRequest request);

    /** Fetches a medicine by ID. */
    ApiResponse<MedicineResponse> getMedicineById(UUID medicineId);

    /** Lists medicines with optional name, category, and stock-status filters. */
    ApiResponse<List<MedicineResponse>> getMedicines(
            String medicineName, MedicineCategory category, MedicineStockStatus stockStatus);

    /** Soft-deletes a medicine. */
    ApiResponse<Void> deleteMedicine(UUID medicineId);

    /** Returns all medicine categories. */
    ApiResponse<List<MedicineCategory>> getCategories();

    /** Returns distinct manufacturer names. */
    ApiResponse<List<String>> getManufacturers();

    /** Returns medicine id and name pairs for dropdowns. */
    ApiResponse<List<MedicineNameResponse>> getMedicineNames();

    /** Returns total stock summary grouped by category. */
    ApiResponse<StockSummaryResponse> getStockSummary();

    /** Returns stock count for a single category. */
    ApiResponse<CategoryStockCountResponse> getStockCountByCategory(MedicineCategory category);

    /** Lists medicines currently in low stock. */
    ApiResponse<List<MedicineResponse>> getLowStockMedicines();

    /** Dashboard page – Medicine Stock Availability card. */
    ApiResponse<DashboardMedicineStockResponse> getDashboardMedicineStock(Integer lowStockLimit);

    /** Deducts stock quantity (e.g. when medicine is sold). */
    ApiResponse<MedicineResponse> deductStock(UUID medicineId, StockAdjustRequest request);

    /** Restores stock quantity (e.g. when an invoice is cancelled). */
    ApiResponse<MedicineResponse> restoreStock(UUID medicineId, StockAdjustRequest request);

}
