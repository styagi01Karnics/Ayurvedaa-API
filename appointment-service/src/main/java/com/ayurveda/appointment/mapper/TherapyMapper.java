package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.enums.TherapyMasterStatus;

@Component
public class TherapyMapper {

    public TherapyMaster toEntity(CreateTherapyRequest request, TherapyMasterStatus status) {
        if (request == null) {
            return null;
        }

        return TherapyMaster.builder()
                .categoryId(request.getCategoryId())
                .therapyName(request.getName().trim())
                .description(request.getDescription())
                .status(status)
                .durationMinutes(request.getDurationMinutes())
                .price(request.getPrice())
                .build();
    }

    public TherapyResponse toResponse(TherapyMaster entity, String categoryName) {
        if (entity == null) {
            return null;
        }

        return TherapyResponse.builder()
                .id(entity.getId())
                .name(entity.getTherapyName())
                .therapyName(entity.getTherapyName())
                .therapyCode(entity.getTherapyCode())
                .categoryId(entity.getCategoryId())
                .categoryName(categoryName)
                .status(entity.getStatus())
                .durationMinutes(entity.getDurationMinutes())
                .price(entity.getPrice())
                .description(entity.getDescription())
                .build();
    }

    public TherapyResponse toResponse(TherapyMaster entity) {
        return toResponse(entity, null);
    }

}
