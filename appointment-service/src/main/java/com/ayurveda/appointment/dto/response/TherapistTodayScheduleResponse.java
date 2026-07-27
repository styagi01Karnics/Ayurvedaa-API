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
public class TherapistTodayScheduleResponse {

    private UUID therapistId;

    private LocalDate date;

    private long totalSlots;

    private List<TherapistTodaySlotResponse> slots;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TherapistTodaySlotResponse {

        private UUID appointmentTherapyId;

        private LocalTime scheduleTime;

        private Integer sessionDuration;

        private TherapyStatus therapyStatus;

        private UUID patientId;

        private String patientName;

        private String patientMobileNumber;

        private List<String> therapies;

        private String treatmentCategoryName;

    }

}
