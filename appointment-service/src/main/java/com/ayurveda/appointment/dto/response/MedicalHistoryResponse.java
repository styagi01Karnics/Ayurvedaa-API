package com.ayurveda.appointment.dto.response;

import java.util.List;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicalHistoryResponse {

    private String pastMedicalConditions;

    private String pastSurgeries;

    private String currentMedications;

    private List<String> allergies;

    private String familyHistory;

}
