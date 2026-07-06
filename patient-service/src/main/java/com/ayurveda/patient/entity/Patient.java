package com.ayurveda.patient.entity;

import com.ayurveda.common.BaseEntity;
import com.ayurveda.patient.enums.Gender;
import com.ayurveda.patient.enums.IdProofType;

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

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mst_patient")
public class Patient extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String patientCode;

    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private LocalDate dateOfBirth;

    private Integer age;

    @Column(length = 50)
    private String preferredLanguage;

    @Column(nullable = false, length = 15)
    private String mobileNumber;

    @Column(length = 150)
    private String email;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String city;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String emergencyContactName;

    @Column(length = 50)
    private String emergencyRelationship;

    @Column(length = 15)
    private String emergencyPhoneNumber;

    @Enumerated(EnumType.STRING)
    private IdProofType idProofType;
    
    @Column(length = 50)
    private String idProofNumber;

    @Column(length = 100)
    private String occupation;

    @Column(length = 255)
    private String insuranceDetails;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

}
