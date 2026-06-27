package com.ayurveda.appointment.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentTherapyRequest {

    @NotNull
    private UUID bookingId;

    @NotNull
    private UUID treatmentCategoryId;

    @NotNull
    private UUID assignedTherapistId;

    @NotNull
    private LocalDate scheduleDate;

    @NotNull
    private LocalTime scheduleTime;

    @NotNull
    private Integer sessionDuration;

    @NotNull
    private Integer sessionFrequency;

    private String therapyInstructions;

    private String remarks;

    /**
     * Recommended Therapies
     */
    @NotEmpty
    private List<UUID> therapyIds;

}