package com.ayurveda.patient.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientCountResponse {

    private long totalPatients;
    private long activePatients;
    private long inactivePatients;

}
