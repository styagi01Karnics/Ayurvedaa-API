package com.ayurveda.appointment.entity;

import java.util.UUID;

import com.ayurveda.appointment.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false, unique = true, length = 20)
    private String therapyCode;

    @Column(nullable = false, unique = true)
    private String therapyName;

    @Column(length = 500)
    private String description;

    @Builder.Default
    private Boolean active = true;

}
