package com.ayurveda.patient.enums;

/**
 * Shared gender-filtering rule for medical-history option enums.
 * FEMALE → ALL + FEMALE; MALE → ALL + MALE; OTHER → ALL only.
 */
final class MedicalHistoryGenderFilter {

    private MedicalHistoryGenderFilter() {
    }

    static boolean applies(ApplicableGender applicableGender, Gender gender) {
        if (applicableGender == ApplicableGender.ALL) {
            return true;
        }
        if (gender == null || gender == Gender.OTHER) {
            return false;
        }
        return applicableGender.name().equals(gender.name());
    }

}
