package com.ayurveda.patient.service.impl;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.util.ResponseUtil;
import com.ayurveda.patient.constant.PatientMessages;
import com.ayurveda.patient.dto.response.MedicalHistoryOptionResponse;
import com.ayurveda.patient.dto.response.MedicalHistoryOptionsResponse;
import com.ayurveda.patient.enums.AllergyCode;
import com.ayurveda.patient.enums.DiseaseCode;
import com.ayurveda.patient.enums.Gender;
import com.ayurveda.patient.enums.MedicationCode;
import com.ayurveda.patient.enums.SurgeryCode;
import com.ayurveda.patient.service.MedicalHistoryService;

import lombok.extern.slf4j.Slf4j;

/**
 * Builds gender-filtered medical-history option lists from fixed enums.
 */
@Slf4j
@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    @Override
    public ApiResponse<MedicalHistoryOptionsResponse> getOptions(Gender gender) {
        if (gender == null) {
            throw new BadRequestException(PatientMessages.GENDER_REQUIRED);
        }

        log.info("Fetching medical history options for gender={}", gender);

        MedicalHistoryOptionsResponse options = MedicalHistoryOptionsResponse.builder()
                .gender(gender)
                .pastMedicalConditions(toOptions(
                        DiseaseCode.forGender(gender),
                        DiseaseCode::getCode,
                        DiseaseCode::getDisplayName))
                .pastSurgeries(toOptions(
                        SurgeryCode.forGender(gender),
                        SurgeryCode::getCode,
                        SurgeryCode::getDisplayName))
                .currentMedications(toOptions(
                        MedicationCode.forGender(gender),
                        MedicationCode::getCode,
                        MedicationCode::getDisplayName))
                .allergies(toOptions(
                        AllergyCode.forGender(gender),
                        AllergyCode::getCode,
                        AllergyCode::getDisplayName))
                .build();

        log.info(
                "Medical history options ready for gender={}: conditions={}, surgeries={}, medications={}, allergies={}",
                gender,
                options.getPastMedicalConditions().size(),
                options.getPastSurgeries().size(),
                options.getCurrentMedications().size(),
                options.getAllergies().size());

        return ResponseUtil.success(PatientMessages.MEDICAL_HISTORY_OPTIONS_FETCHED, options);
    }

    private static <T> List<MedicalHistoryOptionResponse> toOptions(
            List<T> values,
            Function<T, String> codeFn,
            Function<T, String> nameFn) {
        return values.stream()
                .map(value -> MedicalHistoryOptionResponse.builder()
                        .code(codeFn.apply(value))
                        .name(nameFn.apply(value))
                        .build())
                .toList();
    }

}
