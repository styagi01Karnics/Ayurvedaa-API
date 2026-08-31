package com.ayurveda.auth.constant;

public final class AuthMessages {

    private AuthMessages() {
    }

    public static final String HOSPITAL_ONBOARDED_SUCCESSFULLY =
            "Hospital onboarded successfully. Schema provisioned.";
    public static final String HOSPITAL_ADMIN_CREATED_SUCCESSFULLY =
            "Hospital admin created successfully.";
    public static final String PLATFORM_BOOTSTRAPPED_SUCCESSFULLY =
            "Platform super admin bootstrapped successfully.";
    public static final String HOSPITALS_FETCHED_SUCCESSFULLY = "Hospitals fetched successfully.";
    public static final String HOSPITAL_STATUS_UPDATED = "Hospital status updated successfully.";
    public static final String ROLE_CREATED_SUCCESSFULLY = "Role created successfully.";
    public static final String ROLE_UPDATED_SUCCESSFULLY = "Role updated successfully.";
    public static final String ROLE_DELETED_SUCCESSFULLY = "Role deleted successfully.";
    public static final String ROLES_FETCHED_SUCCESSFULLY = "Roles fetched successfully.";
    public static final String UI_PAGES_FETCHED_SUCCESSFULLY = "UI pages fetched successfully.";
    public static final String LOGIN_SUCCESSFUL = "Login successful.";
    public static final String PASSWORD_RESET_TOKEN_IF_ACCOUNT_EXISTS =
            "If an account exists, a password reset token has been generated.";
    public static final String PASSWORD_RESET_TOKEN_GENERATED =
            "Password reset token generated. In production this will be sent by email.";
    public static final String PASSWORD_RESET_SUCCESSFUL = "Password reset successful.";
    public static final String USER_REGISTERED_SUCCESSFULLY = "User registered successfully.";

    public static final String TENANT_CODE_ALREADY_EXISTS = "Tenant code already exists: ";
    public static final String STATE_REQUIRED = "State is required.";
    public static final String HOSPITAL_NAME_REQUIRED = "Hospital name is required.";
    public static final String INVALID_STATE =
            "Unsupported state. Use name or code (e.g. Delhi/DL, Odisha/OD, Uttarakhand/UK): ";
    public static final String CANNOT_BUILD_TENANT_CODE_FROM_NAME =
            "Cannot build tenant code from hospital name. Use a name starting with letters (e.g. Ganesha Ayurveda).";
    public static final String SCHEMA_ALREADY_EXISTS = "Schema name already exists: ";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists: ";
    public static final String TENANT_NOT_ACTIVE = "Tenant is not active.";
    public static final String USER_ACCOUNT_NOT_ACTIVE_WITH_STATUS = "User account is not active: ";
    public static final String USER_ACCOUNT_NOT_ACTIVE = "User account is not active.";
    public static final String INVALID_CREDENTIALS = "Invalid username/email or password.";
    public static final String NEW_PASSWORD_CONFIRM_MISMATCH =
            "New password and confirm password do not match.";
    public static final String TENANT_NOT_FOUND = "Tenant not found.";
    public static final String TENANT_NOT_FOUND_WITH_CODE = "Tenant not found: ";
    public static final String HOSPITAL_NOT_FOUND = "Hospital not found.";
    public static final String INVALID_OR_EXPIRED_RESET_TOKEN =
            "Invalid or expired reset token.";
    public static final String TENANT_NOT_AVAILABLE_FOR_REGISTRATION =
            "Tenant is not available for user registration.";
    public static final String USER_EXISTS_FOR_TENANT_EMAIL =
            "User already exists for this tenant with email: ";
    public static final String CANNOT_CREATE_SUPER_ADMIN_VIA_TENANT_REGISTRATION =
            "Cannot create SUPER_ADMIN via tenant registration.";
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String ONLY_ADMIN_ALLOWED =
            "Only admin or super admin can perform this action.";
    public static final String ONLY_SUPER_ADMIN_ALLOWED =
            "Only platform super admin can perform this action.";
    public static final String SUPER_ADMIN_ALREADY_EXISTS =
            "Platform super admin already exists. Bootstrap is disabled.";
    public static final String INVALID_TENANT_CODE_FOR_SCHEMA =
            "Tenant code cannot be used to build a database schema name.";
    public static final String INVALID_SCHEMA_NAME = "Invalid schema name: ";
    public static final String RESERVED_SCHEMA_NAME = "Reserved schema name cannot be used: ";
    public static final String ROLE_NOT_FOUND = "Role not found.";
    public static final String ROLE_CODE_ALREADY_EXISTS = "Role code already exists for this hospital: ";
    public static final String SYSTEM_ROLE_CANNOT_BE_DELETED = "System roles cannot be deleted.";
    public static final String UI_PAGES_REQUIRED = "At least one UI page code is required.";
    public static final String INVALID_UI_PAGE_CODES = "Invalid UI page code(s): ";
    public static final String TENANT_ROLE_NOT_FOUND = "Tenant role not found for this hospital.";
    public static final String TENANT_ROLE_REQUIRED_FOR_NON_ADMIN =
            "tenantRoleId is required when creating non-admin users.";
    public static final String CANNOT_ASSIGN_ROLE_ON_PLATFORM_TENANT =
            "Custom roles are only for hospital tenants.";
    public static final String PLATFORM_TENANT_REQUIRED =
            "Platform tenant is not bootstrapped yet.";
    public static final String CANNOT_MODIFY_PLATFORM_AS_HOSPITAL =
            "Platform tenant cannot be managed as a hospital.";
    public static final String TENANT_REQUIRED_FOR_HOSPITAL_LOGIN =
            "tenantCode is required for hospital user login.";
    public static final String TENANT_NOT_ALLOWED_FOR_SUPER_ADMIN_LOGIN =
            "Do not send tenantCode for platform Super Admin login.";
    public static final String SUPER_ADMIN_LOGIN_ONLY_WITHOUT_TENANT =
            "Only platform Super Admin can login without tenant. Hospital users must send tenantCode.";
    public static final String HOSPITAL_LOGIN_CANNOT_USE_PLATFORM_TENANT =
            "Hospital login cannot use the platform tenant.";
    public static final String USER_NOT_FOUND_FOR_TENANT =
            "Invalid username/email or password for this hospital.";
    public static final String USER_UPDATED_SUCCESSFULLY = "User updated successfully.";
    public static final String USER_STATUS_UPDATED_SUCCESSFULLY = "User status updated successfully.";
    public static final String USER_DELETED_SUCCESSFULLY = "User deleted successfully.";
    public static final String PASSWORD_CHANGED_SUCCESSFULLY = "Password changed successfully.";
    public static final String PROFILE_UPDATED_SUCCESSFULLY = "Profile updated successfully.";
    public static final String HOSPITAL_ADMINS_FETCHED = "Hospital admins fetched successfully.";
    public static final String HOSPITAL_PROVISION_RETRIED =
            "Hospital schema provision retried successfully.";
    public static final String CURRENT_PASSWORD_INCORRECT = "Current password is incorrect.";
    public static final String CANNOT_DELETE_SELF = "You cannot delete your own account.";
    public static final String CANNOT_MODIFY_SUPER_ADMIN = "Cannot modify platform super admin via this API.";
    public static final String HOSPITAL_NOT_IN_FAILED_STATE =
            "Retry provision is only allowed when hospital status is FAILED.";
    public static final String TENANT_REQUIRED_FOR_HOSPITAL_FORGOT_PASSWORD =
            "tenantCode is required for hospital user forgot-password.";

}
