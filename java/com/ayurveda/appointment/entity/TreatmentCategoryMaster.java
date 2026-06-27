package com.ayurveda.appointment.entity;

import com.ayurveda.appointment.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mst_treatment_category")
public class TreatmentCategoryMaster extends BaseEntity {
	
	@Column(nullable = false, unique = true, length = 20)
    private String categoryCode;

    @Column(nullable = false, unique = true)
    private String categoryName;

    @Column(length = 500)
    private String description;

    @Builder.Default
    private Boolean active = true;

}