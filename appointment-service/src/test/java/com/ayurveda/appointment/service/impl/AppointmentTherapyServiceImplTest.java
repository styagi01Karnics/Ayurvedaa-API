package com.ayurveda.appointment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ayurveda.appointment.client.PatientServiceClient;
import com.ayurveda.appointment.client.TherapistServiceClient;
import com.ayurveda.appointment.common.Constants;
import com.ayurveda.appointment.dto.request.UpdateAppointmentTherapyStatusRequest;
import com.ayurveda.appointment.dto.response.AppointmentTherapyResponse;
import com.ayurveda.appointment.entity.AppointmentTherapy;
import com.ayurveda.appointment.entity.Treatment;
import com.ayurveda.appointment.enums.TherapyStatus;
import com.ayurveda.appointment.enums.TreatmentStatus;
import com.ayurveda.appointment.mapper.AppointmentTherapyMapper;
import com.ayurveda.appointment.repository.AppointmentBookingRepository;
import com.ayurveda.appointment.repository.AppointmentTherapyRecommendationRepository;
import com.ayurveda.appointment.repository.AppointmentTherapyRepository;
import com.ayurveda.appointment.repository.TherapyRepository;
import com.ayurveda.appointment.repository.TreatmentCategoryRepository;
import com.ayurveda.appointment.repository.TreatmentRepository;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.activity.ActivityLogPublisher;
import com.ayurveda.common.exception.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class AppointmentTherapyServiceImplTest {

    @Mock
    private AppointmentTherapyRepository appointmentTherapyRepository;
    @Mock
    private AppointmentTherapyRecommendationRepository appointmentTherapyRecommendationRepository;
    @Mock
    private AppointmentBookingRepository appointmentBookingRepository;
    @Mock
    private TreatmentRepository treatmentRepository;
    @Mock
    private TreatmentCategoryRepository treatmentCategoryRepository;
    @Mock
    private TherapyRepository therapyRepository;
    @Mock
    private TherapistServiceClient therapistServiceClient;
    @Mock
    private AppointmentTherapyMapper appointmentTherapyMapper;
    @Mock
    private PatientServiceClient patientServiceClient;
    @Mock
    private ActivityLogPublisher activityLogPublisher;

    @InjectMocks
    private AppointmentTherapyServiceImpl appointmentTherapyService;

    private UUID therapyId;
    private UUID treatmentId;
    private UUID patientId;
    private UUID therapistId;
    private UUID categoryId;

    @BeforeEach
    void setUp() {
        therapyId = UUID.randomUUID();
        treatmentId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        therapistId = UUID.randomUUID();
        categoryId = UUID.randomUUID();
    }

    @Test
    void updateStatus_byAppointmentTherapyId_updatesTherapy() {
        AppointmentTherapy therapy = AppointmentTherapy.builder()
                .patientId(patientId)
                .treatmentCategoryId(categoryId)
                .assignedTherapistId(therapistId)
                .scheduleDate(LocalDate.now())
                .scheduleTime(LocalTime.of(10, 0))
                .sessionDuration(45)
                .sessionFrequency(7)
                .therapyStatus(TherapyStatus.SCHEDULED)
                .build();
        ReflectionTestUtils.setField(therapy, "id", therapyId);

        UpdateAppointmentTherapyStatusRequest request = UpdateAppointmentTherapyStatusRequest.builder()
                .therapyStatus(TherapyStatus.COMPLETED)
                .build();

        when(appointmentTherapyRepository.findByIdAndDeletedFalse(therapyId))
                .thenReturn(Optional.of(therapy));
        when(appointmentTherapyRepository.save(therapy)).thenReturn(therapy);
        when(appointmentTherapyMapper.toResponse(eq(therapy), any()))
                .thenReturn(AppointmentTherapyResponse.builder()
                        .therapyId(therapyId)
                        .therapyStatus(TherapyStatus.COMPLETED)
                        .build());
        when(appointmentTherapyRecommendationRepository.findByAppointmentTherapyId(therapyId))
                .thenReturn(List.of());
        when(treatmentCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());
        when(therapistServiceClient.getTherapistById(therapistId))
                .thenReturn(ApiResponse.success(null));
        when(patientServiceClient.getPatientById(patientId))
                .thenReturn(ApiResponse.success(null));

        ApiResponse<AppointmentTherapyResponse> response =
                appointmentTherapyService.updateAppointmentTherapyStatus(therapyId, request);

        assertTrue(response.isSuccess());
        assertEquals(TherapyStatus.COMPLETED, therapy.getTherapyStatus());
        verify(treatmentRepository, never()).findByIdAndDeletedFalse(any());
        verify(treatmentRepository, never()).save(any());
    }

    @Test
    void updateStatus_byTreatmentId_updatesTreatment() {
        Treatment treatment = Treatment.builder()
                .patientId(patientId)
                .treatmentPlanId(UUID.randomUUID())
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(7))
                .totalSessions(7)
                .completedSessions(2)
                .remainingSessions(5)
                .assignedTherapistId(therapistId)
                .treatmentStatus(TreatmentStatus.SCHEDULED)
                .build();
        ReflectionTestUtils.setField(treatment, "id", treatmentId);

        UpdateAppointmentTherapyStatusRequest request = UpdateAppointmentTherapyStatusRequest.builder()
                .therapyStatus(TherapyStatus.COMPLETED)
                .build();

        when(appointmentTherapyRepository.findByIdAndDeletedFalse(treatmentId))
                .thenReturn(Optional.empty());
        when(treatmentRepository.findByIdAndDeletedFalse(treatmentId))
                .thenReturn(Optional.of(treatment));
        when(treatmentRepository.save(treatment)).thenReturn(treatment);
        when(therapistServiceClient.getTherapistById(therapistId))
                .thenReturn(ApiResponse.success(null));
        when(patientServiceClient.getPatientById(patientId))
                .thenReturn(ApiResponse.success(null));

        ApiResponse<AppointmentTherapyResponse> response =
                appointmentTherapyService.updateAppointmentTherapyStatus(treatmentId, request);

        assertTrue(response.isSuccess());
        assertEquals(TreatmentStatus.COMPLETED, treatment.getTreatmentStatus());
        assertEquals(7, treatment.getCompletedSessions());
        assertEquals(0, treatment.getRemainingSessions());
        assertEquals(treatmentId, response.getData().getTherapyId());
        assertEquals(TherapyStatus.COMPLETED, response.getData().getTherapyStatus());
        verify(appointmentTherapyRepository, never()).save(any());
    }

    @Test
    void updateStatus_unknownId_throwsNotFound() {
        UUID unknownId = UUID.fromString("c05c9c7d-dc8f-4280-b704-a8421de12634");
        UpdateAppointmentTherapyStatusRequest request = UpdateAppointmentTherapyStatusRequest.builder()
                .therapyStatus(TherapyStatus.COMPLETED)
                .build();

        when(appointmentTherapyRepository.findByIdAndDeletedFalse(unknownId))
                .thenReturn(Optional.empty());
        when(treatmentRepository.findByIdAndDeletedFalse(unknownId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                () -> appointmentTherapyService.updateAppointmentTherapyStatus(unknownId, request));

        assertTrue(ex.getMessage().contains(Constants.APPOINTMENT_THERAPY_NOT_FOUND_WITH_ID));
        assertTrue(ex.getMessage().contains(unknownId.toString()));
    }
}
