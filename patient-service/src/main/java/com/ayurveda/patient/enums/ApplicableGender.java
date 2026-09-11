package com.ayurveda.patient.enums;

/**
 * Marks which gender a medical-history option applies to.
 * {@link #ALL} options are returned for every gender; {@link #MALE}/{@link #FEMALE}
 * are returned only for that gender (plus {@link #ALL}).
 */
public enum ApplicableGender {

    ALL,
    MALE,
    FEMALE

}
