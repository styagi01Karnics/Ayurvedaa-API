package com.ayurveda.doctor.dto.request;

import java.math.BigDecimal;

import com.ayurveda.doctor.enums.DoctorStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import com.ayurveda.common.validation.ValidName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDoctorRequest {

    @NotBlank(message = "Name is required")
    @ValidName(allowPeriod = true)
    private String name;

    @NotBlank(message = "Specialization is required")
    @Size(max = 150, message = "Specialization must not exceed 150 characters")
    private String specialization;

    private DoctorStatus status;

    @NotNull(message = "Consultation fees is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Consultation fees must be 0 or more")
    private BigDecimal consultationFees;

    @NotNull(message = "Follow up fees is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Follow up fees must be 0 or more")
    private BigDecimal followUpFees;

    @NotBlank(message = "Availability is required")
    @Size(max = 255, message = "Availability must not exceed 255 characters")
    private String availability;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit mobile number")
    private String mobileNumber;

    @NotBlank(message = "Email is required")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;

    @Size(min = 3, max = 20, message = "User code must be between 3 and 20 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])[A-Za-z0-9][A-Za-z0-9_-]*$",
            message = "User code can contain only letters, digits, - and _, must include a letter, and cannot be only numbers")
    private String userCode;

}
