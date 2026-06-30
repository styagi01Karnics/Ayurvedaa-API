package com.ayurveda.appointment.dto.request;

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
public class CreateDoshaRequest {

    @NotBlank(message = "Dosha name is required")
    @Size(max = 100)
    private String name;

    private String elements;

    private String characteristics;

    @Builder.Default
    private Boolean active = true;

}
