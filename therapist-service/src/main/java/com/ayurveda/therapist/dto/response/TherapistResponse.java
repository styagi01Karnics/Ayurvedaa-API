package com.ayurveda.therapist.dto.response;

import java.util.List;
import java.util.UUID;

import com.ayurveda.therapist.enums.TherapistStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TherapistResponse {

    private UUID id;
    private String name;
    private String therapistName;
    private String therapistCode;
    private TherapistStatus status;
    private List<AssignedTherapyResponse> assignedTherapies;

}
