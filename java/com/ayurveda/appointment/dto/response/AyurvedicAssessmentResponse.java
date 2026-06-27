package com.ayurveda.appointment.dto.response;

import java.util.List;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AyurvedicAssessmentResponse {

    private String doshaType;

    private List<String> bodyConstitution;

    private String currentImbalances;

}
