package com.ayurveda.appointment.dto.request;

import com.ayurveda.appointment.enums.TherapyStatus;

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
public class UpdateAppointmentTherapyStatusRequest {

    @NotNull(message = "Therapy status is required")
    private TherapyStatus therapyStatus;

}
