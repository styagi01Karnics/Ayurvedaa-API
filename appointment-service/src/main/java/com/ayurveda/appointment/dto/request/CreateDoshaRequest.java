package com.ayurveda.appointment.dto.request;

import com.ayurveda.appointment.enums.DoshaMasterStatus;
import com.ayurveda.common.validation.ValidName;
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
    @ValidName
    private String name;

    private String elements;

    private String characteristics;

    private DoshaMasterStatus status;

}
