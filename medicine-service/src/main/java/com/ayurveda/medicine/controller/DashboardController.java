package com.ayurveda.medicine.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.medicine.dto.response.DashboardMedicineStockResponse;
import com.ayurveda.medicine.service.MedicineService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(
        name = "Dashboard",
        description = "APIs used on the main Dashboard page (Medicine Stock Availability card).")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Validated
public class DashboardController {

    private final MedicineService medicineService;

    @Operation(
            summary = "Dashboard – Medicine Stock Availability card",
            description = """
                    For the Dashboard page "Medicine Stock Availability" widget.

                    Returns:
                    - totalStock
                    - tablets / syrups / powder quantities
                    - statusBreakdown (inStock, outOfStock, lowStock) for the status bar
                    - lowStockItems preview (default 5; use lowStockLimit to change)

                    For View All low stock, use GET /api/v1/medicines/low-stock.
                    """)
    @GetMapping("/medicine-stock")
    public ResponseEntity<ApiResponse<DashboardMedicineStockResponse>> getMedicineStock(
            @RequestParam(required = false, defaultValue = "5") Integer lowStockLimit) {

        return ResponseEntity.ok(medicineService.getDashboardMedicineStock(lowStockLimit));
    }

}
