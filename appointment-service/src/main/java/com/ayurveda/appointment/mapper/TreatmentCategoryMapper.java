package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateTreatmentCategoryRequest;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;
import com.ayurveda.appointment.entity.TreatmentCategoryMaster;
import com.ayurveda.appointment.enums.TreatmentCategoryStatus;

@Component
public class TreatmentCategoryMapper {

    public TreatmentCategoryMaster toEntity(CreateTreatmentCategoryRequest request) {

        if (request == null) {
            return null;
        }

        TreatmentCategoryStatus status =
                request.getStatus() != null ? request.getStatus() : TreatmentCategoryStatus.ACTIVE;

        return TreatmentCategoryMaster.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .status(status)
                .build();
    }

    public TreatmentCategoryResponse toResponse(TreatmentCategoryMaster entity) {

        if (entity == null) {
            return null;
        }

        return TreatmentCategoryResponse.builder()
                .id(entity.getId())
                .categoryCode(entity.getCategoryCode())
                .categoryName(entity.getCategoryName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .build();
    }

}
