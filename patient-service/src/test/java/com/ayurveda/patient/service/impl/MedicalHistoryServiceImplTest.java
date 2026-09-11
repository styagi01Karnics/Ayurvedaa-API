package com.ayurveda.patient.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.patient.constant.PatientMessages;
import com.ayurveda.patient.dto.response.MedicalHistoryOptionResponse;
import com.ayurveda.patient.dto.response.MedicalHistoryOptionsResponse;
import com.ayurveda.patient.enums.Gender;

class MedicalHistoryServiceImplTest {

    private final MedicalHistoryServiceImpl service = new MedicalHistoryServiceImpl();

    @Test
    void getOptionsForFemaleIncludesFemaleSpecificConditionsAndSurgeries() {
        MedicalHistoryOptionsResponse data = service.getOptions(Gender.FEMALE).getData();

        assertEquals(Gender.FEMALE, data.getGender());
        Set<String> conditions = codes(data.getPastMedicalConditions());
        assertTrue(conditions.contains("PCOS"));
        assertTrue(conditions.contains("ENDOMETRIOSIS"));
        assertTrue(conditions.contains("DIABETES_TYPE_2"));
        assertFalse(conditions.contains("PROSTATE_CANCER"));

        Set<String> surgeries = codes(data.getPastSurgeries());
        assertTrue(surgeries.contains("C_SECTION"));
        assertTrue(surgeries.contains("HYSTERECTOMY"));
        assertTrue(surgeries.contains("APPENDECTOMY"));
    }

    @Test
    void getOptionsForMaleIncludesMaleSpecificConditionsAndExcludesFemale() {
        MedicalHistoryOptionsResponse data = service.getOptions(Gender.MALE).getData();

        Set<String> conditions = codes(data.getPastMedicalConditions());
        assertTrue(conditions.contains("BENIGN_PROSTATIC_HYPERPLASIA"));
        assertTrue(conditions.contains("ERECTILE_DYSFUNCTION"));
        assertFalse(conditions.contains("PCOS"));
        assertFalse(conditions.contains("UTERINE_FIBROIDS"));

        Set<String> surgeries = codes(data.getPastSurgeries());
        assertTrue(surgeries.contains("APPENDECTOMY"));
        assertFalse(surgeries.contains("C_SECTION"));
    }

    @Test
    void getOptionsForOtherReturnsOnlyAllGenderOptions() {
        MedicalHistoryOptionsResponse data = service.getOptions(Gender.OTHER).getData();

        Set<String> conditions = codes(data.getPastMedicalConditions());
        assertTrue(conditions.contains("HYPERTENSION"));
        assertFalse(conditions.contains("PCOS"));
        assertFalse(conditions.contains("PROSTATE_CANCER"));

        Set<String> surgeries = codes(data.getPastSurgeries());
        assertTrue(surgeries.contains("GALLBLADDER_REMOVAL"));
        assertFalse(surgeries.contains("HYSTERECTOMY"));
    }

    @Test
    void getOptionsRejectsNullGender() {
        BadRequestException ex = assertThrows(
                BadRequestException.class,
                () -> service.getOptions(null));
        assertEquals(PatientMessages.GENDER_REQUIRED, ex.getMessage());
    }

    private static Set<String> codes(java.util.List<MedicalHistoryOptionResponse> options) {
        return options.stream()
                .map(MedicalHistoryOptionResponse::getCode)
                .collect(Collectors.toSet());
    }

}
