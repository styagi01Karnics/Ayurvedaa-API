package com.ayurveda.therapist.mapper;

import com.ayurveda.therapist.dto.response.TherapistResponse;
import com.ayurveda.therapist.entity.Therapist;

public final class TherapistMapper {

    private TherapistMapper() {
    }

    public static TherapistResponse toResponse(Therapist therapist) {
        return TherapistResponse.builder()
                .id(therapist.getId())
                .therapistName(therapist.getTherapistName())
                .therapistCode(therapist.getTherapistCode())
                .specialization(therapist.getSpecialization())
                .mobileNumber(therapist.getMobileNumber())
                .email(therapist.getEmail())
                .qualification(therapist.getQualification())
                .therapyRoom(therapist.getTherapyRoom())
                .active(therapist.getActive())
                .createdAt(therapist.getCreatedAt())
                .updatedAt(therapist.getUpdatedAt())
                .build();
    }

}
