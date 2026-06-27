package com.ayurveda.appointment.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentPlanResponse {

    private String investigationSuggested;

    private String planTaken;

}
