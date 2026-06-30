package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateDoshaRequest;
import com.ayurveda.appointment.dto.response.DoshaResponse;
import com.ayurveda.appointment.entity.DoshaMaster;

@Component
public class DoshaMapper {

    public DoshaMaster toEntity(CreateDoshaRequest request) {
        return DoshaMaster.builder()
                .name(request.getName())
                .elements(request.getElements())
                .characteristics(request.getCharacteristics())
                .active(request.getActive() != null ? request.getActive() : true)
                .build();
    }

    public DoshaResponse toResponse(DoshaMaster dosha) {
        return DoshaResponse.builder()
                .id(dosha.getId())
                .name(dosha.getName())
                .elements(dosha.getElements())
                .characteristics(dosha.getCharacteristics())
                .active(dosha.getActive())
                .build();
    }

}
