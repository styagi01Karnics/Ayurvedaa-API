package com.ayurveda.billing.client;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.ayurveda.billing.dto.client.MedicineClientResponse;
import com.ayurveda.billing.dto.client.StockAdjustClientRequest;
import com.ayurveda.common.ApiResponse;

@FeignClient(name = "medicine-service", url = "${services.medicine.url}")
public interface MedicineServiceClient {

    @GetMapping("/api/v1/medicines/{medicineId}")
    ApiResponse<MedicineClientResponse> getMedicineById(@PathVariable("medicineId") UUID medicineId);

    @PostMapping("/api/v1/medicines/{medicineId}/stock/deduct")
    ApiResponse<MedicineClientResponse> deductStock(
            @PathVariable("medicineId") UUID medicineId,
            @RequestBody StockAdjustClientRequest request);

    @PostMapping("/api/v1/medicines/{medicineId}/stock/restore")
    ApiResponse<MedicineClientResponse> restoreStock(
            @PathVariable("medicineId") UUID medicineId,
            @RequestBody StockAdjustClientRequest request);

}
