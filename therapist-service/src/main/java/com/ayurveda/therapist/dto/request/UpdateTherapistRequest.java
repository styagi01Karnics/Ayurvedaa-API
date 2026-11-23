package com.ayurveda.therapist.dto.request;

import java.util.List;
import java.util.UUID;

import com.ayurveda.therapist.enums.TherapistStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.ayurveda.common.validation.ValidName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTherapistRequest {

    @NotBlank(message = "Name is required")
    @ValidName
    private String name;

    private TherapistStatus status;

    @NotEmpty(message = "At least one assigned therapy id is required")
    private List<@NotNull UUID> assignedTherapyIds;

}
