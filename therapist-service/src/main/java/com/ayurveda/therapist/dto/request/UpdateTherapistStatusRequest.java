package com.ayurveda.therapist.dto.request;

import com.ayurveda.therapist.enums.TherapistStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTherapistStatusRequest {

    @NotNull(message = "Status is required")
    private TherapistStatus status;

}
