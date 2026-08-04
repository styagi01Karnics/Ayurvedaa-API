package com.ayurveda.appointment.dto.request;

import com.ayurveda.appointment.enums.FollowUpStatus;

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
public class UpdateFollowUpStatusRequest {

    @NotNull(message = "Status is required")
    private FollowUpStatus status;

}
