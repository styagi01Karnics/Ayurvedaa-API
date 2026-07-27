package com.ayurveda.doctor.dto.request;

import java.math.BigDecimal;

import com.ayurveda.doctor.enums.DoctorStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDoctorRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150, message = "Name must not exceed 150 characters")
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

}
