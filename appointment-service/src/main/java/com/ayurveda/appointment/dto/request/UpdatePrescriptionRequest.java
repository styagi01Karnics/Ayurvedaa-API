package com.ayurveda.appointment.dto.request;

import java.util.List;
import java.util.UUID;

import com.ayurveda.appointment.dto.request.CreatePrescriptionRequest.PrescriptionMedicineItemRequest;
import com.ayurveda.appointment.dto.request.CreatePrescriptionRequest.PrescriptionNextFollowUpRequest;
import com.ayurveda.appointment.dto.request.CreatePrescriptionRequest.PrescriptionTherapySuggestionRequest;

import jakarta.validation.Valid;
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
public class UpdatePrescriptionRequest {

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

}
