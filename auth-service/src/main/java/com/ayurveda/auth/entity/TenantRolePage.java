package com.ayurveda.auth.entity;

import com.ayurveda.common.BaseEntity;
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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "tenant_role_pages",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_tenant_role_pages",
                        columnNames = {"tenant_role_id", "ui_page_id"})
        })
public class TenantRolePage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_role_id", nullable = false)
    private TenantRole tenantRole;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ui_page_id", nullable = false)
    private UiPage uiPage;

}
