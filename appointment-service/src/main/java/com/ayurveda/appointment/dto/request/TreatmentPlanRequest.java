package com.ayurveda.appointment.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentPlanRequest {

    private String investigationSuggested;

    private String planTaken;

}