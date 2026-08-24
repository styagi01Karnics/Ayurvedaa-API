package com.ayurveda.auth.entity;

import com.ayurveda.auth.enums.TenantStatus;
import com.ayurveda.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
@Table(name = "tenants")
public class Tenant extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String tenantCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String address;

    /**
     * Postgres schema for this hospital (e.g. hosp_gan).
     * Platform tenant uses {@code public}.
     */
    @Column(nullable = false, unique = true, length = 63)
    private String schemaName;

    /** True only for the development-company platform tenant. */
    @Column(nullable = false)
    @Builder.Default
    private Boolean platform = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TenantStatus status;

    @Column(length = 500)
    private String provisionMessage;

}
