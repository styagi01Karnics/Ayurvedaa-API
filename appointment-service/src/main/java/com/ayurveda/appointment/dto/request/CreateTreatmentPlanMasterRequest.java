package com.ayurveda.appointment.dto.request;

import com.ayurveda.appointment.enums.TreatmentPlanMasterStatus;

import com.ayurveda.common.validation.ValidName;
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
    @ValidName
    private String name;

    private TreatmentPlanMasterStatus status;

}
