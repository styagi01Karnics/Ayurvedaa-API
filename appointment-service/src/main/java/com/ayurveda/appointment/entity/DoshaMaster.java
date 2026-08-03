package com.ayurveda.appointment.entity;

import com.ayurveda.appointment.enums.DoshaMasterStatus;
import com.ayurveda.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
@Table(name = "mst_doshas")
public class DoshaMaster extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String elements;

    @Column(columnDefinition = "TEXT")
    private String characteristics;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DoshaMasterStatus status;

    /**
     * Legacy column kept after active→status migration.
     * Hibernate ddl-auto does not drop old columns; NOT NULL active caused POST 409.
     */
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @PrePersist
    @PreUpdate
    void syncLegacyActiveAndDefaults() {
        if (status == null) {
            status = DoshaMasterStatus.ACTIVE;
        }
        active = status == DoshaMasterStatus.ACTIVE;
        if (getDeleted() == null) {
            setDeleted(false);
        }
    }

    @PostLoad
    void fillStatusFromLegacyActive() {
        if (status == null) {
            status = Boolean.FALSE.equals(active)
                    ? DoshaMasterStatus.INACTIVE
                    : DoshaMasterStatus.ACTIVE;
        }
        if (getDeleted() == null) {
            setDeleted(false);
        }
    }

}
