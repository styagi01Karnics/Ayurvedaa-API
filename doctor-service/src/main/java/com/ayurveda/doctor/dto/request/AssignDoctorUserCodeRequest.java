package com.ayurveda.doctor.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AssignDoctorUserCodeRequest {

    @NotBlank(message = "User code is required")
    @Size(min = 3, max = 20, message = "User code must be between 3 and 20 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9][A-Za-z0-9_-]*$",
            message = "User code can contain only letters, digits, - and _, must include a letter, and cannot be only numbers")
    private String userCode;
}
