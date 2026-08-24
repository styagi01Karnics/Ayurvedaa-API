package com.ayurveda.auth.enums;

public enum TenantStatus {
    /** Schema/tables being created for the hospital. */
    PROVISIONING,
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    /** Schema provision failed; can retry. */
    FAILED
}
