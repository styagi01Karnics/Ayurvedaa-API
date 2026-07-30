package com.ayurveda.medicine.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.medicine.dto.request.CreateMedicineRequest;
import com.ayurveda.medicine.dto.request.CreateMedicineRequestList;
import com.ayurveda.medicine.dto.request.StockAdjustRequest;
import com.ayurveda.medicine.dto.request.UpdateMedicineRequest;
import com.ayurveda.medicine.dto.response.CategoryStockCountResponse;
import com.ayurveda.medicine.dto.response.MedicineNameResponse;
import com.ayurveda.medicine.dto.response.MedicineResponse;
import com.ayurveda.medicine.dto.response.StockSummaryResponse;
import com.ayurveda.medicine.enums.MedicineCategory;
import com.ayurveda.medicine.enums.MedicineStockStatus;
import com.ayurveda.medicine.service.MedicineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Medicine Inventory", description = "Medicine inventory management APIs")
@RestController
@RequestMapping("/api/v1/medicines")
@RequiredArgsConstructor
@Validated
public class MedicineController {

    private final MedicineService medicineService;

    @Operation(
            summary = "Add medicine(s)",
            description = "Accepts one medicine object or an array of medicines. Each saved record gets a unique id.")
    @PostMapping
    public ResponseEntity<ApiResponse<List<MedicineResponse>>> createMedicines(
            @Valid @RequestBody CreateMedicineRequestList request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(medicineService.createMedicines(request.getMedicines()));
    }

    @Operation(summary = "Update medicine")
    @PutMapping("/{medicineId}")
    public ResponseEntity<ApiResponse<MedicineResponse>> updateMedicine(
            @PathVariable UUID medicineId,
            @Valid @RequestBody UpdateMedicineRequest request) {

        return ResponseEntity.ok(medicineService.updateMedicine(medicineId, request));
    }

    @Operation(
            summary = "List medicines",
            description = "Supports search by medicine name and filters for category and stock status.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicineResponse>>> getMedicines(
            @RequestParam(required = false) String medicineName,
            @RequestParam(required = false) MedicineCategory category,
            @RequestParam(required = false) MedicineStockStatus stockStatus) {

        return ResponseEntity.ok(medicineService.getMedicines(medicineName, category, stockStatus));
    }

    @Operation(summary = "Total stock summary by category")
    @GetMapping("/stock/summary")
    public ResponseEntity<ApiResponse<StockSummaryResponse>> getStockSummary() {
        return ResponseEntity.ok(medicineService.getStockSummary());
    }

    @Operation(summary = "Total stock count for a category")
    @GetMapping("/stock/category/{category}")
    public ResponseEntity<ApiResponse<CategoryStockCountResponse>> getStockCountByCategory(
            @PathVariable MedicineCategory category) {

        return ResponseEntity.ok(medicineService.getStockCountByCategory(category));
    }

    @Operation(summary = "Get low stock medicine details")
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<MedicineResponse>>> getLowStockMedicines() {
        return ResponseEntity.ok(medicineService.getLowStockMedicines());
    }

    @Operation(summary = "List medicine categories (dropdown)")
    @GetMapping("/meta/categories")
    public ResponseEntity<ApiResponse<List<MedicineCategory>>> getCategories() {
        return ResponseEntity.ok(medicineService.getCategories());
    }

    @Operation(summary = "List manufacturers (dropdown)")
    @GetMapping("/meta/manufacturers")
    public ResponseEntity<ApiResponse<List<String>>> getManufacturers() {
        return ResponseEntity.ok(medicineService.getManufacturers());
    }

    @Operation(summary = "List medicine id + name (dropdown)")
    @GetMapping("/meta/names")
    public ResponseEntity<ApiResponse<List<MedicineNameResponse>>> getMedicineNames() {
        return ResponseEntity.ok(medicineService.getMedicineNames());
    }

    @Operation(summary = "Get medicine by ID")
    @GetMapping("/{medicineId}")
    public ResponseEntity<ApiResponse<MedicineResponse>> getMedicineById(
            @PathVariable UUID medicineId) {

        return ResponseEntity.ok(medicineService.getMedicineById(medicineId));
    }

    @Operation(summary = "Deduct stock (used by billing when medicine is sold)")
    @PostMapping("/{medicineId}/stock/deduct")
    public ResponseEntity<ApiResponse<MedicineResponse>> deductStock(
            @PathVariable UUID medicineId,
            @Valid @RequestBody StockAdjustRequest request) {

        return ResponseEntity.ok(medicineService.deductStock(medicineId, request));
    }

    @Operation(summary = "Restore stock (used when invoice with medicine is cancelled/deleted)")
    @PostMapping("/{medicineId}/stock/restore")
    public ResponseEntity<ApiResponse<MedicineResponse>> restoreStock(
            @PathVariable UUID medicineId,
            @Valid @RequestBody StockAdjustRequest request) {

        return ResponseEntity.ok(medicineService.restoreStock(medicineId, request));
    }

    @Operation(summary = "Delete medicine")
    @DeleteMapping("/{medicineId}")
    public ResponseEntity<ApiResponse<Void>> deleteMedicine(@PathVariable UUID medicineId) {
        return ResponseEntity.ok(medicineService.deleteMedicine(medicineId));
    }

}
