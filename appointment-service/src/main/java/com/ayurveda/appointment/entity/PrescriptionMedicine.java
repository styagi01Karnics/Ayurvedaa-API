package com.ayurveda.appointment.entity;

import java.util.UUID;

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
@Table(name = "prescription_medicines")
public class PrescriptionMedicine extends BaseEntity {

    @Column(nullable = false)
    private UUID prescriptionId;

    /** Optional medicine inventory id from medicine-service. */
    private UUID medicineId;

    @Column(nullable = false, length = 255)
    private String medicineName;

    @Column(length = 100)
    private String dosage;

    @Column(length = 100)
    private String frequency;

    @Column(length = 100)
    private String duration;

    @Column(length = 500)
    private String notes;

}
