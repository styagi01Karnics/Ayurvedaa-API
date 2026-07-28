package com.ayurveda.therapist.mapper;

import java.util.List;

import com.ayurveda.therapist.dto.response.TherapistResponse;
import com.ayurveda.therapist.entity.Therapist;

public final class TherapistMapper {

    private TherapistMapper() {
    }

    public static TherapistResponse toResponse(Therapist therapist) {
        return toResponse(therapist, null);
    }

    public static TherapistResponse toResponse(Therapist therapist, Integer serialNo) {
        List<String> therapies = therapist.getAssignedTherapies() == null
                ? List.of()
                : List.copyOf(therapist.getAssignedTherapies());

        return TherapistResponse.builder()
                .serialNo(serialNo)
                .id(therapist.getId())
                .name(therapist.getTherapistName())
                .therapistName(therapist.getTherapistName())
                .therapistCode(therapist.getTherapistCode())
                .status(therapist.getStatus())
                .assignedTherapies(therapies)
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
