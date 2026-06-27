package com.ayurveda.appointment.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.enums.BookingStatus;
import com.ayurveda.appointment.enums.Gender;
import com.ayurveda.appointment.enums.IdProofType;
import com.ayurveda.appointment.enums.WorkflowStep;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentBookingResponse {

    private UUID id;

    private String patientCode;

    private String fullName;

    private Gender gender;

    private LocalDate dateOfBirth;

    private Integer age;

    private String preferredLanguage;

    private LocalDate registrationDate;

    private UUID assignedDoctorId;

    private List<String> consultationTypes;

    private String mobileNumber;

    private String email;

    private String state;

    private String city;

    private String permanentAddress;

    private String emergencyContactName;

    private String emergencyRelationship;

    private String emergencyPhoneNumber;

    private IdProofType idProofType;

    private String idProofNumber;

    private String occupation;

    private String insuranceDetails;

    private WorkflowStep workflowStep;

    private BookingStatus bookingStatus;

}