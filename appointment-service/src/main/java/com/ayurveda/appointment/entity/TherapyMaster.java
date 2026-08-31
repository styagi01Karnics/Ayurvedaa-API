package com.ayurveda.appointment.entity;

import java.math.BigDecimal;
import java.util.UUID;

import com.ayurveda.appointment.enums.TherapyMasterStatus;
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
@Table(name = "mst_therapy")
public class TherapyMaster extends BaseEntity {

    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = false, unique = true, length = 50)
    private String therapyCode;

    @Column(nullable = false, unique = true)
    private String therapyName;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TherapyMasterStatus status;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

}
