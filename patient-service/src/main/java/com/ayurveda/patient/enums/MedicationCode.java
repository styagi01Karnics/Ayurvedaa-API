package com.ayurveda.patient.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Current medication codes for medical-history options.
 */
public enum MedicationCode {

    ASHWAGANDHA("Ashwagandha", ApplicableGender.ALL),
    IRON_SUPPLEMENTS("Iron Supplements", ApplicableGender.ALL),
    MULTIVITAMINS("Multivitamins", ApplicableGender.ALL),
    THYROID_MEDICATION("Thyroid Medication", ApplicableGender.ALL),
    METFORMIN("Metformin", ApplicableGender.ALL),
    LEVOTHYROXINE("Levothyroxine", ApplicableGender.ALL),
    VITAMIN_D("Vitamin D", ApplicableGender.ALL),
    CALCIUM("Calcium", ApplicableGender.ALL),
    NONE("None", ApplicableGender.ALL),
    OTHER("Other", ApplicableGender.ALL);

    private final String displayName;
    private final ApplicableGender applicableGender;

    MedicationCode(String displayName, ApplicableGender applicableGender) {
        this.displayName = displayName;
        this.applicableGender = applicableGender;
    }

    public String getCode() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public ApplicableGender getApplicableGender() {
        return applicableGender;
    }

    public static List<MedicationCode> forGender(Gender gender) {
        return Arrays.stream(values())
                .filter(code -> code.appliesTo(gender))
                .toList();
    }

    public boolean appliesTo(Gender gender) {
        return MedicalHistoryGenderFilter.applies(applicableGender, gender);
    }

}
