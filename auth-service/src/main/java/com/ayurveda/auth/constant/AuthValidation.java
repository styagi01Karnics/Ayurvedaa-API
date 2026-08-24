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

}
