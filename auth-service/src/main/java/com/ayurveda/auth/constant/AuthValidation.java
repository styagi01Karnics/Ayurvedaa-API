package com.ayurveda.auth.constant;

/**
 * Auth-service validation patterns.
 */
public final class AuthValidation {

    private AuthValidation() {
    }

    /** Username must be a Gmail address. */
    public static final String GMAIL =
            "^[A-Za-z0-9._%+-]+@gmail\\.com$";

    public static final String GMAIL_MESSAGE =
            "Username must be a valid Gmail address (example@gmail.com)";

    /** Hospital outbound mailbox (Gmail or Microsoft / Outlook). */
    public static final String EMAIL =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static final String EMAIL_OPTIONAL =
            "^$|^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static final String EMAIL_MESSAGE =
            "Enter a valid email address (Gmail or Microsoft)";

}
