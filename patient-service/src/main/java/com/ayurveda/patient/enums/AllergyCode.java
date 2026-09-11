package com.ayurveda.patient.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Allergy codes for medical-history options.
 */
public enum AllergyCode {

    PENICILLIN("Penicillin", ApplicableGender.ALL),
    AMOXICILLIN("Amoxicillin", ApplicableGender.ALL),
    ASPIRIN("Aspirin", ApplicableGender.ALL),
    IBUPROFEN("Ibuprofen", ApplicableGender.ALL),
    LATEX("Latex", ApplicableGender.ALL),
    DUST("Dust", ApplicableGender.ALL),
    POLLEN("Pollen", ApplicableGender.ALL),
    MILK("Milk", ApplicableGender.ALL),
    PEANUTS("Peanuts", ApplicableGender.ALL),
    TREE_NUTS("Tree Nuts", ApplicableGender.ALL),
    EGGS("Eggs", ApplicableGender.ALL),
    SHELLFISH("Shellfish", ApplicableGender.ALL),
    OTHER("Other", ApplicableGender.ALL),
    NONE("None", ApplicableGender.ALL);

    private final String displayName;
    private final ApplicableGender applicableGender;

    AllergyCode(String displayName, ApplicableGender applicableGender) {
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

    public static List<AllergyCode> forGender(Gender gender) {
        return Arrays.stream(values())
                .filter(code -> code.appliesTo(gender))
                .toList();
    }

    public boolean appliesTo(Gender gender) {
        return MedicalHistoryGenderFilter.applies(applicableGender, gender);
    }

}
