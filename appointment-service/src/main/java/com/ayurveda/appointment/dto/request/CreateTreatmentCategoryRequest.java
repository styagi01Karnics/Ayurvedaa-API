package com.ayurveda.appointment.dto.request;

import com.ayurveda.appointment.enums.TreatmentCategoryStatus;
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

    private TreatmentCategoryStatus status;

}
