package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateConsultationTypeRequest;
import com.ayurveda.appointment.dto.response.ConsultationTypeItemResponse;
import com.ayurveda.appointment.dto.response.ConsultationTypeResponse;
import com.ayurveda.appointment.entity.ConsultationTypeMaster;
import com.ayurveda.appointment.enums.ConsultationTypeMasterStatus;

@Component
public class ConsultationTypeMapper {

    public ConsultationTypeMaster toEntity(CreateConsultationTypeRequest request) {
        ConsultationTypeMasterStatus status =
                request.getStatus() != null ? request.getStatus() : ConsultationTypeMasterStatus.ACTIVE;

        ConsultationTypeMaster entity = ConsultationTypeMaster.builder()
                .name(request.getName().trim())
                .status(status)
                .build();
        entity.setDeleted(false);
        return entity;
    }

    public ConsultationTypeResponse toResponse(ConsultationTypeMaster entity) {
        return ConsultationTypeResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(entity.getStatus())
                .build();
    }

    public ConsultationTypeItemResponse toItem(ConsultationTypeMaster entity) {
        return ConsultationTypeItemResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

}
