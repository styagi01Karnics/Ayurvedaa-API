package com.ayurveda.appointment.dto.request;

import com.ayurveda.appointment.enums.TreatmentStatus;

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
public class UpdateTreatmentStatusRequest {

    @NotNull(message = "Treatment status is required")
    private TreatmentStatus treatmentStatus;

}
