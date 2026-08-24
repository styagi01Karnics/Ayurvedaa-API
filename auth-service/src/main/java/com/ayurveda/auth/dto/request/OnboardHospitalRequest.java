package com.ayurveda.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Super Admin onboards a hospital (tenant) and provisions its Postgres schema. */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardHospitalRequest {

    @NotBlank(message = "Hospital code is required")
    @Size(max = 50)
    private String tenantCode;

    @NotBlank(message = "Hospital name is required")
    @Size(max = 150)
    private String name;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 255)
    private String address;

}
