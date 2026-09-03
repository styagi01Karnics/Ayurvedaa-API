package com.ayurveda.appointment.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

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
public class RescheduleAppointmentBookingRequest {

    /**
     * Optional — defaults to the existing booking's patient when omitted.
     */
    private UUID patientId;

    @NotNull(message = "Registration date is required")
    private LocalDate registrationDate;

    @NotNull(message = "Slot time is required")
    private LocalTime slotTime;

    /**
     * Optional — defaults to the existing booking's doctor when omitted.
     */
    private UUID assignedDoctorId;

    /**
     * Optional — keeps existing consultation types when omitted/empty.
     */
    private List<UUID> consultationTypeIds;

}
