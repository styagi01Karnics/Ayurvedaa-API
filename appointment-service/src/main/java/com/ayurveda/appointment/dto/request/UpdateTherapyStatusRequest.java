package com.ayurveda.appointment.dto.request;

import com.ayurveda.appointment.enums.TherapyMasterStatus;
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
public class UpdateTherapyStatusRequest {

    @NotNull(message = "Status is required")
    private TherapyMasterStatus status;

}
