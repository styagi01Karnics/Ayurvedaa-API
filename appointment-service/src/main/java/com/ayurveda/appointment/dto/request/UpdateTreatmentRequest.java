package com.ayurveda.appointment.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class UpdateTreatmentRequest {

    @NotNull(message = "Treatment plan id is required")
    private UUID treatmentPlanId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Total sessions is required")
    @Min(value = 1, message = "Total sessions must be at least 1")
    private Integer totalSessions;

    @NotNull(message = "Completed sessions is required")
    @Min(value = 0, message = "Completed sessions cannot be negative")
    private Integer completedSessions;

    @NotNull(message = "Assigned therapist id is required")
    private UUID assignedTherapistId;

}
