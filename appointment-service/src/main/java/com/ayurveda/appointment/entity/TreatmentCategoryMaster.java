package com.ayurveda.appointment.entity;

import com.ayurveda.appointment.enums.TreatmentCategoryStatus;
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
@Table(name = "mst_treatment_category")
public class TreatmentCategoryMaster extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String categoryCode;

    @Column(nullable = false, unique = true)
    private String categoryName;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TreatmentCategoryStatus status;

}
