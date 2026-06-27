package com.ayurveda.appointment.dto.request;

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
public class CreateAppointmentSystemicExaminationRequest {

    @NotNull(message = "Booking Id is required")
    private UUID bookingId;

    private String cardiovascular;

    private String respiratory;

    private String nervous;

    private String abdomenGi;

    private String locomotor;

}