package com.ayurveda.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

/**
 * BaseEntity plus the user who created and last updated the row.
 * Values come from {@link com.ayurveda.common.config.TenantAuditorAware}.
 */
@Getter
@Setter
@MappedSuperclass
public abstract class AuditedEntity extends BaseEntity {

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 150)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "updated_by", length = 150)
    private String updatedBy;

}
