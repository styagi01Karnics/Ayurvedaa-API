package com.ayurveda.therapist.mapper;

import java.util.List;

import com.ayurveda.therapist.dto.response.AssignedTherapyResponse;
import com.ayurveda.therapist.dto.response.TherapistResponse;
import com.ayurveda.therapist.entity.Therapist;

public final class TherapistMapper {

    private TherapistMapper() {
    }

    public static TherapistResponse toResponse(
            Therapist therapist, List<AssignedTherapyResponse> assignedTherapies) {
        return TherapistResponse.builder()
                .id(therapist.getId())
                .name(therapist.getTherapistName())
                .therapistName(therapist.getTherapistName())
                .therapistCode(therapist.getTherapistCode())
                .status(therapist.getStatus())
                .assignedTherapies(assignedTherapies == null ? List.of() : assignedTherapies)
                .build();
    }

}
