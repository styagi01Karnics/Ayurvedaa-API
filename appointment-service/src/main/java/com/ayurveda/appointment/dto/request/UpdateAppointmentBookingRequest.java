package com.ayurveda.appointment.dto.request;

import java.util.UUID;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentBookingRequest
        extends CreateAppointmentBookingRequest {

    private UUID bookingId;
}