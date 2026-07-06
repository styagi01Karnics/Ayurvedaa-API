package com.ayurveda.therapist.entity;

import com.ayurveda.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "mst_therapist")
public class Therapist extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String therapistName;

    @Column(nullable = false, unique = true, length = 100)
    private String therapistCode;

    @Column(length = 150)
    private String specialization;

    @Column(length = 15)
    private String mobileNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String qualification;

    @Column(length = 100)
    private String therapyRoom;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

}
