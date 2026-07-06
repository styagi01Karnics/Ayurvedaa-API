package com.ayurveda.therapist.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTherapistRequest {

    @NotBlank(message = "Therapist name is required")
    @Size(max = 150, message = "Therapist name must not exceed 150 characters")
    private String therapistName;

    @Size(max = 150, message = "Specialization must not exceed 150 characters")
    private String specialization;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be 10 to 15 digits")
    private String mobileNumber;

    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Size(max = 100, message = "Qualification must not exceed 100 characters")
    private String qualification;

    @Size(max = 100, message = "Therapy room must not exceed 100 characters")
    private String therapyRoom;

}
