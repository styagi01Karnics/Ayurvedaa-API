package com.ayurveda.auth.constant;

public final class AuthMessages {

    private AuthMessages() {
    }

    public static final String TENANT_REGISTERED_SUCCESSFULLY = "Tenant registered successfully.";
    public static final String LOGIN_SUCCESSFUL = "Login successful.";
    public static final String SIGN_UP_SUCCESSFUL = "Sign up successful.";
    public static final String PASSWORD_RESET_TOKEN_IF_ACCOUNT_EXISTS =
            "If an account exists, a password reset token has been generated.";
    public static final String PASSWORD_RESET_TOKEN_GENERATED =
            "Password reset token generated. In production this will be sent by email.";
    public static final String PASSWORD_RESET_SUCCESSFUL = "Password reset successful.";
    public static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully.";

    public static final String TENANT_CODE_ALREADY_EXISTS = "Tenant code already exists: ";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists: ";
    public static final String TENANT_NOT_ACTIVE = "Tenant is not active.";
    public static final String USER_ACCOUNT_NOT_ACTIVE_WITH_STATUS = "User account is not active: ";
    public static final String USER_ACCOUNT_NOT_ACTIVE = "User account is not active.";
    public static final String INVALID_CREDENTIALS = "Invalid username/email or password.";
    public static final String PASSWORD_CONFIRM_MISMATCH =
            "Password and confirm password do not match.";
    public static final String NEW_PASSWORD_CONFIRM_MISMATCH =
            "New password and confirm password do not match.";
    public static final String TENANT_NOT_FOUND = "Tenant not found.";
    public static final String TENANT_NOT_FOUND_WITH_CODE = "Tenant not found: ";
    public static final String EMAIL_ALREADY_REGISTERED_FOR_TENANT =
            "Email already registered for this tenant.";
    public static final String INVALID_OR_EXPIRED_RESET_TOKEN =
            "Invalid or expired reset token.";
    public static final String TENANT_NOT_AVAILABLE_FOR_REGISTRATION =
            "Tenant is not available for user registration.";
    public static final String USER_EXISTS_FOR_TENANT_EMAIL =
            "User already exists for this tenant with email: ";
    public static final String CANNOT_CREATE_SUPER_ADMIN_VIA_TENANT_REGISTRATION =
            "Cannot create SUPER_ADMIN via tenant registration.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String ONLY_TENANT_ADMIN_ALLOWED =
            "Only tenant admin can perform this action.";

}
