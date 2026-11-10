package com.ayurveda.therapist.dto.request;

import java.util.List;
import java.util.UUID;

import com.ayurveda.therapist.enums.TherapistStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTherapistRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
    private String name;

    private TherapistStatus status;

    @NotEmpty(message = "At least one assigned therapy id is required")
    private List<@NotNull UUID> assignedTherapyIds;

}
