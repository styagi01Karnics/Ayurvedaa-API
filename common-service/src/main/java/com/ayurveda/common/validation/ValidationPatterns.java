package com.ayurveda.common.validation;

/**
 * Shared Jakarta {@code @Pattern} / regex constants used across services.
 * Keeps Sonar from flagging duplicated regex literals in patient & appointment DTOs.
 */
public final class ValidationPatterns {

    private ValidationPatterns() {
    }

    public static final String FULL_NAME =
            "^[A-Za-z .'-]+$";

    public static final String FULL_NAME_ALPHABETS_SPACES =
            "^[A-Za-z ]+$";

    public static final String ALPHABETS_AND_SPACES_OPTIONAL =
            "^[A-Za-z ]*$";

    public static final String PLACE_NAME_OPTIONAL =
            "^[A-Za-z .-]*$";

    public static final String PERSON_NAME_OPTIONAL =
            "^[A-Za-z .'-]*$";

    public static final String MOBILE_IN =
            "^[6-9]\\d{9}$";

    public static final String OCCUPATION =
            "^[A-Za-z0-9 .,&()/-]*$";

    public static final String GENDER =
            "^(MALE|FEMALE|OTHER)$";

    public static final String ID_PROOF_TYPE =
            "^(AADHAAR|PAN|PASSPORT|DRIVING_LICENSE|VOTER_ID)?$";

    public static final String AADHAAR =
            "^\\d{12}$";

    public static final String PAN =
            "^[A-Z]{5}[0-9]{4}[A-Z]$";

    public static final String PASSPORT =
            "^[A-Z][0-9]{7}$";

    public static final String DRIVING_LICENSE =
            "^[A-Z]{2}[0-9]{13}$";

    public static final String VOTER_ID =
            "^[A-Z]{3}[0-9]{7}$";

}
