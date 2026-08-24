package com.ayurveda.auth.entity;

import com.ayurveda.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Hospital-defined role created by hospital admin (not platform Super Admin).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "tenant_roles",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tenant_roles_tenant_code",
                        columnNames = {"tenant_id", "role_code"})
        })
public class TenantRole extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 50)
    private String roleCode;

    @Column(nullable = false, length = 100)
    private String roleName;

    @Column(length = 255)
    private String description;

    /** Seeded system role (e.g. HOSPITAL_ADMIN) — cannot be deleted. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean systemRole = false;

    /** Matches Figma Active toggle on role. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

}
