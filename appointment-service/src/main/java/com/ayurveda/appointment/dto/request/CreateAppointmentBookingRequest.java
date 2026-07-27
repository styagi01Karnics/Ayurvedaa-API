package com.ayurveda.appointment.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class CreateAppointmentBookingRequest {

    @Valid
    @NotNull(message = "Patient details are required")
    private CreatePatientDetailsRequest patient;

    @NotNull(message = "Registration date is required")
    private LocalDate registrationDate;

    @NotNull(message = "Slot time is required")
    private LocalTime slotTime;

    @NotNull(message = "Assigned doctor id is required")
    private UUID assignedDoctorId;

    @NotEmpty(message = "At least one consultation type is required")
    private List<String> consultationTypes;

}
