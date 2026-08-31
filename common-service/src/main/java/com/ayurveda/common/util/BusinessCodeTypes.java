package com.ayurveda.common.util;

/**
 * Type segments for hospital business codes:
 * {@code {tenantCode}-{TYPE}-{#####}} (e.g. {@code GAN-DL-PT-00001}).
 */
public final class BusinessCodeTypes {

    public static final String PATIENT = "PT";
    public static final String DOCTOR = "DOC";
    public static final String THERAPIST = "THP";
    public static final String THERAPY = "TH";
    public static final String TREATMENT_CATEGORY = "TC";
    public static final String INVOICE = "INV";
    public static final String ATTENDANCE = "ATT";

    private BusinessCodeTypes() {
    }

}
