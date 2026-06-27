package com.ayurveda.appointment.entity;

import com.ayurveda.appointment.common.BaseEntity;
import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.Gender;
import com.ayurveda.appointment.enums.IdProofType;
import com.ayurveda.appointment.enums.WorkflowStep;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointment_bookings")
public class AppointmentBooking extends BaseEntity {

    // ========================
    // Basic Information
    // ========================

    @Column(nullable = false,length = 150)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private Integer age;

    @Column(length = 50)
    private String preferredLanguage;

    @Column(nullable = false)
    private LocalDate registrationDate;

    @Column(nullable = false)
    private UUID assignedDoctorId;

    // ========================
    // Contact Information
    // ========================

    @Column(nullable = false,length = 15)
    private String mobileNumber;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String state;

    @Column(length = 100)
    private String city;

    @Column(columnDefinition = "TEXT")
    private String permanentAddress;

    // ========================
    // Emergency Contact
    // ========================

    @Column(length = 100)
    private String emergencyContactName;

    @Column(length = 50)
    private String emergencyRelationship;

    @Column(length = 15)
    private String emergencyPhoneNumber;

    // ========================
    // Identification
    // ========================

    @Column(unique = true,length = 30)
    private String patientCode;

    @Enumerated(EnumType.STRING)
    private IdProofType idProofType;

    @Column(length = 50)
    private String idProofNumber;

    @Column(length = 100)
    private String occupation;

    @Column(length = 255)
    private String insuranceDetails;

    // ========================
    // Workflow
    // ========================

    @Enumerated(EnumType.STRING)
    private WorkflowStep workflowStep;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

}