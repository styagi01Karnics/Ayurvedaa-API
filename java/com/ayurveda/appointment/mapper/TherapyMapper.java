package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.entity.TherapyMaster;

@Component
public class TherapyMapper {

    public TherapyMaster toEntity(CreateTherapyRequest request) {

        if (request == null) {
            return null;
        }

        return TherapyMaster.builder()
                .categoryId(request.getCategoryId())
                .therapyName(request.getTherapyName())
                .description(request.getDescription())
                .active(request.getActive())
                .build();
    }

    public TherapyResponse toResponse(TherapyMaster entity) {

        if (entity == null) {
            return null;
        }

        return TherapyResponse.builder()
                .id(entity.getId())
                .categoryId(entity.getCategoryId())
                .therapyCode(entity.getTherapyCode())
                .therapyName(entity.getTherapyName())
                .description(entity.getDescription())
                .active(entity.getActive())
                .build();
    }

}