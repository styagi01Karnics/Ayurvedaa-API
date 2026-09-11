package com.ayurveda.patient.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Past surgery codes for medical-history options.
 */
public enum SurgeryCode {

    APPENDECTOMY("Appendectomy", ApplicableGender.ALL),
    C_SECTION("C-Section", ApplicableGender.FEMALE),
    HYSTERECTOMY("Hysterectomy", ApplicableGender.FEMALE),
    OVARIAN_CYST_REMOVAL("Ovarian Cyst Removal", ApplicableGender.FEMALE),
    GALLBLADDER_REMOVAL("Gallbladder Removal", ApplicableGender.ALL),
    NONE("None", ApplicableGender.ALL),
    OTHER("Other", ApplicableGender.ALL);

    private final String displayName;
    private final ApplicableGender applicableGender;

    SurgeryCode(String displayName, ApplicableGender applicableGender) {
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

    public static List<SurgeryCode> forGender(Gender gender) {
        return Arrays.stream(values())
                .filter(code -> code.appliesTo(gender))
                .toList();
    }

    public boolean appliesTo(Gender gender) {
        return MedicalHistoryGenderFilter.applies(applicableGender, gender);
    }

}
