package com.ayurveda.patient.dto.response;

import java.util.List;

import com.ayurveda.patient.enums.Gender;

import lombok.Builder;
import lombok.Getter;

/**
 * Gender-filtered medical-history option lists for the intake form.
 */
@Getter
@Builder
public class MedicalHistoryOptionsResponse {

    private Gender gender;
    private List<MedicalHistoryOptionResponse> pastMedicalConditions;
    private List<MedicalHistoryOptionResponse> pastSurgeries;
    private List<MedicalHistoryOptionResponse> currentMedications;
    private List<MedicalHistoryOptionResponse> allergies;

}
