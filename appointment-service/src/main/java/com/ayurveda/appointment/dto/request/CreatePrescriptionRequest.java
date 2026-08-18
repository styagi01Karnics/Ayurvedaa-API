package com.ayurveda.appointment.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
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
public class CreatePrescriptionRequest {

    @NotNull(message = "Patient id is required")
    private UUID patientId;

    /** Optional booking this prescription belongs to. */
    private UUID appointmentBookingId;

    private UUID assignedDoctorId;

    @Valid
    private List<PrescriptionMedicineItemRequest> medicines;

    @Valid
    private List<PrescriptionTherapySuggestionRequest> therapySuggestions;

    @Valid
    private PrescriptionNextFollowUpRequest nextFollowUp;

    /** Diagnosis for prescription print view. */
    private String diagnosis;

    /** Free-text notes ("Add texts if any"). */
    private String notes;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionMedicineItemRequest {

        private UUID medicineId;

        @NotBlank(message = "Medicine name is required")
        @Size(max = 255)
        private String medicineName;

        @Size(max = 100)
        private String dosage;

        @Size(max = 100)
        private String frequency;

        @Size(max = 100)
        private String duration;

        @Size(max = 500)
        private String notes;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionTherapySuggestionRequest {

        @NotNull(message = "Therapy category id is required")
        private UUID therapyCategoryId;

        @NotNull(message = "Recommended therapy ids are required")
        private List<UUID> recommendedTherapyIds;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionNextFollowUpRequest {

        /** True when "Set Follow Up" is selected. */
        private Boolean setUpRequired;

        @Size(max = 100)
        private String schedulingOption;

        private String suggestions;
    }

}
