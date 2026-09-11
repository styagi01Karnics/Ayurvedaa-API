package com.ayurveda.patient.service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.patient.dto.response.MedicalHistoryOptionsResponse;
import com.ayurveda.patient.enums.Gender;

/**
 * Medical-history option catalog (gender-filtered enums).
 */
public interface MedicalHistoryService {

    /**
     * Returns common + gender-specific medical-history options for the given gender.
     *
     * @param gender required patient gender ({@code MALE}, {@code FEMALE}, or {@code OTHER})
     */
    ApiResponse<MedicalHistoryOptionsResponse> getOptions(Gender gender);

}
