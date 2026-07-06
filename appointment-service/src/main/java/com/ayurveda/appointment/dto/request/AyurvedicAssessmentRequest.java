package com.ayurveda.appointment.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AyurvedicAssessmentRequest {

    @NotNull(message = "Dosha Id is required")
    private UUID doshaId;

    private List<String> bodyConstitution;

    private String currentImbalances;

}