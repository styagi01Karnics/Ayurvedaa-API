package com.ayurveda.therapist.dto.request;

import java.util.List;

import com.ayurveda.therapist.enums.TherapistStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "At least one assigned therapy is required")
    private List<@NotBlank @Size(max = 150) String> assignedTherapies;

}
