package com.ayurveda.therapist.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AssignedTherapyResponse {

    private UUID id;
    private String name;

}
