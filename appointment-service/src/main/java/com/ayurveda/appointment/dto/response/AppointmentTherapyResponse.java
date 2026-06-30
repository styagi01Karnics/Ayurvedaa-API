package com.ayurveda.appointment.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.enums.TherapyStatus;

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
public class AppointmentTherapyResponse {

    private UUID therapyId;

    private UUID bookingId;

    private UUID treatmentCategoryId;

    private UUID assignedTherapistId;

    private TherapistSummaryResponse assignedTherapist;

    private LocalDate scheduleDate;

    private LocalTime scheduleTime;

    private Integer sessionDuration;

    private Integer sessionFrequency;

    private String therapyInstructions;

    private String remarks;

    private TherapyStatus therapyStatus;

    /**
     * Recommended Therapies
     */
    private List<UUID> therapyIds;

}