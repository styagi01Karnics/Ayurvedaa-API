package com.ayurveda.appointment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDoctorRequest {

    @NotBlank
    @Size(max = 150)
    private String doctorName;

    @NotBlank
    @Size(max = 100)
    private String doctorCode;

    private String specialization;

    private String mobileNumber;

    @Email
    private String email;

    private String qualification;

    private String department;

    private String consultationRoom;

    @Builder.Default
    private Boolean active = true;
}