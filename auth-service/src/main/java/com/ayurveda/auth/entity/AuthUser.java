package com.ayurveda.auth.entity;

import com.ayurveda.auth.enums.Profession;
import com.ayurveda.auth.enums.UserRole;
import com.ayurveda.auth.enums.UserStatus;
import com.ayurveda.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "auth_users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_auth_users_tenant_email", columnNames = {"tenant_id", "email"}),
                @UniqueConstraint(name = "uk_auth_users_tenant_user_code", columnNames = {"tenant_id", "user_code"})
        }
)
public class AuthUser extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /** Canonical login identity (Gmail). */
    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String mobileNumber;

    /** Entered manually when the user is created; required, unique within a tenant (case-insensitive). */
    @Column(nullable = false, length = 30)
    private String userCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Profession profession;

    /** Null until the user sets a password through the reset-password link (doctors). */
    @Column(length = 255)
    private String passwordHash;

    /** System authority used by Spring Security (SUPER_ADMIN, ADMIN, DOCTOR, …). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserRole role;

    /**
     * Hospital custom role for UI page access.
     * Null for SUPER_ADMIN; ADMIN may also use a seeded hospital-admin role.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_role_id")
    private TenantRole tenantRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

}
