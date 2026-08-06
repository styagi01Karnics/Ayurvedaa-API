package com.ayurveda.appointment.dto.request;

import com.ayurveda.appointment.enums.ConsultationTypeMasterStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class CreateConsultationTypeRequest {

    @NotBlank(message = "Consultation type name is required")
    @Size(max = 100)
    private String name;

    private ConsultationTypeMasterStatus status;

}
