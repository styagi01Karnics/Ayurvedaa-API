package com.ayurveda.appointment.dto.request;

import com.ayurveda.appointment.enums.TreatmentPlanMasterStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class CreateTreatmentPlanMasterRequest {

    @NotBlank(message = "Treatment plan name is required")
    @Size(max = 150)
    private String name;

    private TreatmentPlanMasterStatus status;

}
