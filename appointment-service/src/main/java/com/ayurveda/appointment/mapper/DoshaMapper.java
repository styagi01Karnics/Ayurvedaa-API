package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateDoshaRequest;
import com.ayurveda.appointment.dto.response.DoshaResponse;
import com.ayurveda.appointment.entity.DoshaMaster;
import com.ayurveda.appointment.enums.DoshaMasterStatus;

@Component
public class DoshaMapper {

    public DoshaMaster toEntity(CreateDoshaRequest request) {
        DoshaMasterStatus status =
                request.getStatus() != null ? request.getStatus() : DoshaMasterStatus.ACTIVE;

        return DoshaMaster.builder()
                .name(request.getName())
                .elements(request.getElements())
                .characteristics(request.getCharacteristics())
                .status(status)
                .build();
    }

    public DoshaResponse toResponse(DoshaMaster dosha) {
        return DoshaResponse.builder()
                .id(dosha.getId())
                .name(dosha.getName())
                .elements(dosha.getElements())
                .characteristics(dosha.getCharacteristics())
                .status(dosha.getStatus())
                .build();
    }

}
