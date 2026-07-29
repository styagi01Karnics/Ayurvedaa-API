package com.ayurveda.appointment.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

import com.ayurveda.appointment.enums.TherapyMasterStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class UpdateTherapyRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150)
    private String name;

    @NotNull(message = "Category id is required")
    private UUID categoryId;

    private TherapyMasterStatus status;

    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price must be 0 or more")
    private BigDecimal price;

    @Size(max = 500)
    private String description;

}
