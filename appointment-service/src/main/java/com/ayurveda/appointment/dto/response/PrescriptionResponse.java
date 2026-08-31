package com.ayurveda.appointment.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
public class PrescriptionResponse {

    private UUID id;
    private UUID patientId;
    private UUID appointmentBookingId;
    private UUID assignedDoctorId;

    private PatientDetails patient;
    private TreatmentDetails treatment;
    private ConsultantDetails consultant;

    private String diagnosis;
    private String notes;

    private List<PrescriptionMedicineItemResponse> medicines;
    private List<PrescriptionTherapySuggestionResponse> therapySuggestions;
    private PrescriptionNextFollowUpResponse nextFollowUp;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientDetails {
        private UUID id;
        private String patientCode;
        private String fullName;
        private Integer age;
        private String gender;
        private Double weight;
        private Double height;
        private String dietType;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TreatmentDetails {
        private List<String> consultationTypes;
        private LocalDateTime consultationDateTime;
        private LocalDateTime nextAppointmentDateTime;
        /** Current visit number (e.g. 3 in 03/12). */
        private Integer visitNumber;
        /** Total planned visits/sessions (e.g. 12 in 03/12). */
        private Integer totalVisits;
        private String visitDisplay;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsultantDetails {
        private UUID id;
        private String name;
        private String specialization;
        private String qualification;
        private String contactNumber;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionMedicineItemResponse {
        private UUID id;
        private UUID medicineId;
        private String medicineName;
        private String dosage;
        private String frequency;
        private String duration;
        /** Maps to UI "Instruction" (e.g. After food). */
        private String instruction;
        private String notes;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionTherapySuggestionResponse {
        private UUID id;
        private UUID therapyCategoryId;
        private String therapyCategoryName;
        private List<RecommendedTherapyItemResponse> recommendedTherapies;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendedTherapyItemResponse {
        private UUID therapyId;
        private String therapyName;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrescriptionNextFollowUpResponse {
        private Boolean setUpRequired;
        private String schedulingOption;
        private String suggestions;
    }

}
