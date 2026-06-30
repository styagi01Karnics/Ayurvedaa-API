package com.ayurveda.appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTreatmentCategoryRequest {

    @NotBlank
    @Size(max = 100)
    private String categoryName;

    @Size(max = 255)
    private String description;

    @Builder.Default
    private Boolean active = true;

}