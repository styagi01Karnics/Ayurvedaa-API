package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateTreatmentCategoryRequest;
import com.ayurveda.appointment.dto.response.TreatmentCategoryResponse;
import com.ayurveda.appointment.entity.TreatmentCategoryMaster;

@Component
public class TreatmentCategoryMapper {

    public TreatmentCategoryMaster toEntity(CreateTreatmentCategoryRequest request) {

        if (request == null) {
            return null;
        }

        return TreatmentCategoryMaster.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .active(request.getActive())
                .build();
    }

    public TreatmentCategoryResponse toResponse(TreatmentCategoryMaster entity) {

        if (entity == null) {
            return null;
        }

        return TreatmentCategoryResponse.builder()
                .id(entity.getId())
                .categoryName(entity.getCategoryName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .build();
    }

}