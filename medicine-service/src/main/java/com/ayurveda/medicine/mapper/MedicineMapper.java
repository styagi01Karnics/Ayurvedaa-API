package com.ayurveda.medicine.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.medicine.dto.request.CreateMedicineRequest;
import com.ayurveda.medicine.dto.request.UpdateMedicineRequest;
import com.ayurveda.medicine.dto.response.MedicineResponse;
import com.ayurveda.medicine.entity.Medicine;
import com.ayurveda.medicine.enums.MedicineStatus;
import com.ayurveda.medicine.enums.MedicineStockStatus;
import com.ayurveda.medicine.util.MedicineStatusResolver;

@Component
public class MedicineMapper {

    public Medicine toEntity(CreateMedicineRequest request, int defaultThreshold) {
        int threshold = request.getLowStockThreshold() != null
                ? request.getLowStockThreshold()
                : defaultThreshold;
        boolean alertEnabled = Boolean.TRUE.equals(request.getLowStockAlertEnabled());
        MedicineStockStatus stockStatus = MedicineStatusResolver.resolve(request.getQuantity(), threshold);
        MedicineStatus status =
                request.getStatus() != null ? request.getStatus() : MedicineStatus.ACTIVE;

        return Medicine.builder()
                .medicineName(request.getMedicineName().trim())
                .category(request.getCategory())
                .manufacturer(request.getManufacturer().trim())
                .batchNumber(request.getBatchNumber().trim())
                .quantity(request.getQuantity())
                .expiryDate(request.getExpiryDate())
                .purchasePrice(request.getPurchasePrice())
                .sellingPrice(request.getSellingPrice())
                .lowStockAlertEnabled(alertEnabled)
                .lowStockThreshold(threshold)
                .status(status)
                .stockStatus(stockStatus)
                .build();
    }

    public void updateEntity(Medicine medicine, UpdateMedicineRequest request, int defaultThreshold) {
        int threshold = request.getLowStockThreshold() != null
                ? request.getLowStockThreshold()
                : (medicine.getLowStockThreshold() != null ? medicine.getLowStockThreshold() : defaultThreshold);

        medicine.setMedicineName(request.getMedicineName().trim());
        medicine.setCategory(request.getCategory());
        medicine.setManufacturer(request.getManufacturer().trim());
        medicine.setBatchNumber(request.getBatchNumber().trim());
        medicine.setQuantity(request.getQuantity());
        medicine.setExpiryDate(request.getExpiryDate());
        medicine.setPurchasePrice(request.getPurchasePrice());
        medicine.setSellingPrice(request.getSellingPrice());
        medicine.setLowStockAlertEnabled(Boolean.TRUE.equals(request.getLowStockAlertEnabled()));
        medicine.setLowStockThreshold(threshold);
        medicine.setStockStatus(MedicineStatusResolver.resolve(request.getQuantity(), threshold));
        if (request.getStatus() != null) {
            medicine.setStatus(request.getStatus());
        }
    }

    public MedicineResponse toResponse(Medicine medicine) {
        return MedicineResponse.builder()
                .id(medicine.getId())
                .medicineName(medicine.getMedicineName())
                .category(medicine.getCategory())
                .manufacturer(medicine.getManufacturer())
                .batchNumber(medicine.getBatchNumber())
                .stockQuantity(medicine.getQuantity())
                .expiryDate(medicine.getExpiryDate())
                .purchasePrice(medicine.getPurchasePrice())
                .sellingPrice(medicine.getSellingPrice())
                .price(medicine.getSellingPrice())
                .lowStockAlertEnabled(medicine.getLowStockAlertEnabled())
                .lowStockThreshold(medicine.getLowStockThreshold())
                .status(medicine.getStatus())
                .stockStatus(medicine.getStockStatus())
                .build();
    }

}
