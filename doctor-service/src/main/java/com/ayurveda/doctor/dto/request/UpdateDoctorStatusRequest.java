package com.ayurveda.doctor.dto.request;

import com.ayurveda.doctor.enums.DoctorStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateDoctorStatusRequest {

    @NotNull(message = "Status is required")
    private DoctorStatus status;

}
