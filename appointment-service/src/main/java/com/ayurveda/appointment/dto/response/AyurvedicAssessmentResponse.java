package com.ayurveda.appointment.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AyurvedicAssessmentResponse {

    private UUID doshaId;

    private DoshaResponse dosha;

    private List<String> bodyConstitution;

    private String currentImbalances;

}
