package com.ayurveda.appointment.dto.request;

import java.util.List;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AyurvedicAssessmentRequest {

    private String doshaType;

    private List<String> bodyConstitution;

    private String currentImbalances;

}