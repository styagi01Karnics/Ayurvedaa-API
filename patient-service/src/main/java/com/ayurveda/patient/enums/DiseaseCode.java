package com.ayurveda.patient.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Past medical condition codes for medical-history options.
 * Each entry carries a display name and {@link ApplicableGender} for filtering.
 */
public enum DiseaseCode {

    DIABETES_TYPE_1("Diabetes Mellitus Type 1", ApplicableGender.ALL),
    DIABETES_TYPE_2("Diabetes Mellitus Type 2", ApplicableGender.ALL),
    PREDIABETES("Prediabetes", ApplicableGender.ALL),
    HYPERTENSION("Hypertension", ApplicableGender.ALL),
    HYPOTHYROIDISM("Hypothyroidism", ApplicableGender.ALL),
    HYPERTHYROIDISM("Hyperthyroidism", ApplicableGender.ALL),
    HIGH_CHOLESTEROL("High Cholesterol", ApplicableGender.ALL),
    OBESITY("Obesity", ApplicableGender.ALL),
    MIGRAINE("Migraine", ApplicableGender.ALL),
    EPILEPSY("Epilepsy", ApplicableGender.ALL),
    PARKINSONS_DISEASE("Parkinson's Disease", ApplicableGender.ALL),
    ALZHEIMERS_DISEASE("Alzheimer's Disease", ApplicableGender.ALL),
    STROKE("Stroke", ApplicableGender.ALL),
    ASTHMA("Asthma", ApplicableGender.ALL),
    COPD("COPD", ApplicableGender.ALL),
    PNEUMONIA("Pneumonia", ApplicableGender.ALL),
    TUBERCULOSIS("Tuberculosis", ApplicableGender.ALL),
    CHRONIC_GASTRITIS("Chronic Gastritis", ApplicableGender.ALL),
    GERD("GERD", ApplicableGender.ALL),
    PEPTIC_ULCER("Peptic Ulcer", ApplicableGender.ALL),
    IBS("IBS", ApplicableGender.ALL),
    CROHNS_DISEASE("Crohn's Disease", ApplicableGender.ALL),
    ULCERATIVE_COLITIS("Ulcerative Colitis", ApplicableGender.ALL),
    FATTY_LIVER("Fatty Liver", ApplicableGender.ALL),
    HEPATITIS_A("Hepatitis A", ApplicableGender.ALL),
    HEPATITIS_B("Hepatitis B", ApplicableGender.ALL),
    HEPATITIS_C("Hepatitis C", ApplicableGender.ALL),
    CIRRHOSIS("Cirrhosis", ApplicableGender.ALL),
    KIDNEY_STONES("Kidney Stones", ApplicableGender.ALL),
    CHRONIC_KIDNEY_DISEASE("Chronic Kidney Disease", ApplicableGender.ALL),
    URINARY_TRACT_INFECTION("Urinary Tract Infection", ApplicableGender.ALL),
    ARTHRITIS("Arthritis", ApplicableGender.ALL),
    OSTEOARTHRITIS("Osteoarthritis", ApplicableGender.ALL),
    RHEUMATOID_ARTHRITIS("Rheumatoid Arthritis", ApplicableGender.ALL),
    OSTEOPOROSIS("Osteoporosis", ApplicableGender.ALL),
    GOUT("Gout", ApplicableGender.ALL),
    PSORIASIS("Psoriasis", ApplicableGender.ALL),
    ECZEMA("Eczema", ApplicableGender.ALL),
    ACNE("Acne", ApplicableGender.ALL),
    VITILIGO("Vitiligo", ApplicableGender.ALL),
    ANEMIA("Anemia", ApplicableGender.ALL),
    IRON_DEFICIENCY_ANEMIA("Iron Deficiency Anemia", ApplicableGender.ALL),
    VITAMIN_B12_DEFICIENCY("Vitamin B12 Deficiency", ApplicableGender.ALL),
    ANXIETY("Anxiety", ApplicableGender.ALL),
    DEPRESSION("Depression", ApplicableGender.ALL),
    BIPOLAR_DISORDER("Bipolar Disorder", ApplicableGender.ALL),

    PCOS("PCOS", ApplicableGender.FEMALE),
    PCOD("PCOD", ApplicableGender.FEMALE),
    ENDOMETRIOSIS("Endometriosis", ApplicableGender.FEMALE),
    UTERINE_FIBROIDS("Uterine Fibroids", ApplicableGender.FEMALE),
    OVARIAN_CYST("Ovarian Cyst", ApplicableGender.FEMALE),
    PELVIC_INFLAMMATORY_DISEASE("Pelvic Inflammatory Disease", ApplicableGender.FEMALE),
    MENSTRUAL_DISORDER("Menstrual Disorder", ApplicableGender.FEMALE),

    BENIGN_PROSTATIC_HYPERPLASIA("Benign Prostatic Hyperplasia", ApplicableGender.MALE),
    PROSTATITIS("Prostatitis", ApplicableGender.MALE),
    PROSTATE_CANCER("Prostate Cancer", ApplicableGender.MALE),
    ERECTILE_DYSFUNCTION("Erectile Dysfunction", ApplicableGender.MALE),

    OTHER("Other", ApplicableGender.ALL),
    NONE("None", ApplicableGender.ALL);

    private final String displayName;
    private final ApplicableGender applicableGender;

    DiseaseCode(String displayName, ApplicableGender applicableGender) {
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

    /**
     * Returns disease options applicable to the given gender
     * (ALL + gender-specific; OTHER receives ALL only).
     */
    public static List<DiseaseCode> forGender(Gender gender) {
        return Arrays.stream(values())
                .filter(code -> code.appliesTo(gender))
                .toList();
    }

    public boolean appliesTo(Gender gender) {
        return MedicalHistoryGenderFilter.applies(applicableGender, gender);
    }

}
