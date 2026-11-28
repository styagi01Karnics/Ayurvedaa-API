package com.ayurveda.auth.constant;

/**
 * Auth-service validation patterns.
 */
public final class AuthValidation {

    private AuthValidation() {
    }

    /** Username must be a valid email address (any domain). */
    public static final String GMAIL =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static final String GMAIL_MESSAGE =
            "Username must be a valid email address (example@domain.com)";

    /** Hospital outbound mailbox (Gmail or Microsoft / Outlook). */
    public static final String EMAIL =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static final String EMAIL_OPTIONAL =
            "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static final String EMAIL_MESSAGE =
            "Enter a valid email address (Gmail or Microsoft)";

    /** User code: 3-20 characters, letters and digits only, must mix at least one letter and one digit. */
    public static final String USER_CODE =
            "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9]+$";

}
