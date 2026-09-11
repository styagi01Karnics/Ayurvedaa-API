package com.ayurveda.patient.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * Single medical-history option ({@code code} + display {@code name}).
 */
@Getter
@Builder
public class MedicalHistoryOptionResponse {

    private String code;
    private String name;

}
