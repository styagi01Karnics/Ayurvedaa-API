package com.ayurveda.therapist.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
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

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.exception.BadRequestException;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.therapist.client.AppointmentServiceClient;
import com.ayurveda.therapist.dto.client.TherapyMasterClientResponse;
import com.ayurveda.therapist.dto.request.CreateTherapistRequest;
import com.ayurveda.therapist.dto.request.UpdateTherapistRequest;
import com.ayurveda.therapist.dto.request.UpdateTherapistStatusRequest;
import com.ayurveda.therapist.dto.response.TherapistResponse;
import com.ayurveda.therapist.entity.Therapist;
import com.ayurveda.therapist.enums.TherapistStatus;
import com.ayurveda.therapist.repository.TherapistRepository;
import com.ayurveda.therapist.util.TherapistCodeGenerator;

@ExtendWith(MockitoExtension.class)
class TherapistServiceImplTest {

    @Mock
    private TherapistRepository therapistRepository;

    @Mock
    private TherapistCodeGenerator therapistCodeGenerator;

    @Mock
    private AppointmentServiceClient appointmentServiceClient;

    @InjectMocks
    private TherapistServiceImpl therapistService;

    private UUID therapistId;
    private UUID therapyId;
    private Therapist therapist;

    @BeforeEach
    void setUp() {
        therapistId = UUID.randomUUID();
        therapyId = UUID.randomUUID();
        therapist = Therapist.builder()
                .therapistName("Dr. Rahul Verma")
                .therapistCode("GAN-DL-THP-00002")
                .status(TherapistStatus.ACTIVE)
                .assignedTherapyIds(new ArrayList<>(List.of(therapyId)))
                .build();
        ReflectionTestUtils.setField(therapist, "id", therapistId);
    }

    @Test
    void createTherapist_validatesTherapyAndSaves() {
        CreateTherapistRequest request = new CreateTherapistRequest();
        request.setName(" Dr. Rahul Verma ");
        request.setAssignedTherapyIds(List.of(therapyId));

        when(appointmentServiceClient.getTherapyById(therapyId))
                .thenReturn(ApiResponse.success(TherapyMasterClientResponse.builder()
                        .id(therapyId)
                        .name("Kayakalpa")
                        .build()));
        when(therapistCodeGenerator.generate()).thenReturn("GAN-DL-THP-00002");
        when(therapistRepository.save(any(Therapist.class))).thenAnswer(inv -> {
            Therapist saved = inv.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", therapistId);
            return saved;
        });
        when(appointmentServiceClient.getAllTherapies())
                .thenReturn(ApiResponse.success(List.of(TherapyMasterClientResponse.builder()
                        .id(therapyId)
                        .therapyName("Kayakalpa")
                        .build())));

        ApiResponse<TherapistResponse> response = therapistService.createTherapist(request);

        assertTrue(response.isSuccess());
        assertEquals("Dr. Rahul Verma", response.getData().getName());
        assertEquals("Kayakalpa", response.getData().getAssignedTherapies().get(0).getName());
    }

    @Test
    void createTherapist_invalidTherapy_throws() {
        CreateTherapistRequest request = new CreateTherapistRequest();
        request.setName("Therapist");
        request.setAssignedTherapyIds(List.of(therapyId));

        when(appointmentServiceClient.getTherapyById(therapyId))
                .thenReturn(ApiResponse.success(null));

        assertThrows(BadRequestException.class, () -> therapistService.createTherapist(request));
    }

    @Test
    void getAllTherapists_resolvesTherapyNames() {
        when(therapistRepository.findAllByDeletedFalse()).thenReturn(List.of(therapist));
        when(appointmentServiceClient.getAllTherapies())
                .thenReturn(ApiResponse.success(List.of(TherapyMasterClientResponse.builder()
                        .id(therapyId)
                        .name("Kayakalpa")
                        .build())));

        ApiResponse<List<TherapistResponse>> response = therapistService.getAllTherapists();

        assertEquals(1, response.getData().size());
        assertEquals("Kayakalpa", response.getData().get(0).getAssignedTherapies().get(0).getName());
    }

    @Test
    void getAllTherapists_fallsBackToGetByIdWhenBulkFails() {
        when(therapistRepository.findAllByDeletedFalse()).thenReturn(List.of(therapist));
        when(appointmentServiceClient.getAllTherapies()).thenThrow(new RuntimeException("down"));
        when(appointmentServiceClient.getTherapyById(therapyId))
                .thenReturn(ApiResponse.success(TherapyMasterClientResponse.builder()
                        .id(therapyId)
                        .name("Podikizhi")
                        .build()));

        ApiResponse<List<TherapistResponse>> response = therapistService.getAllTherapists();

        assertEquals("Podikizhi", response.getData().get(0).getAssignedTherapies().get(0).getName());
    }

    @Test
    void updateTherapistStatus_updatesStatus() {
        UpdateTherapistStatusRequest request = new UpdateTherapistStatusRequest();
        request.setStatus(TherapistStatus.INACTIVE);

        when(therapistRepository.findByIdAndDeletedFalse(therapistId)).thenReturn(Optional.of(therapist));
        when(therapistRepository.save(therapist)).thenReturn(therapist);
        when(appointmentServiceClient.getAllTherapies()).thenReturn(ApiResponse.success(List.of()));

        ApiResponse<TherapistResponse> response =
                therapistService.updateTherapistStatus(therapistId, request);

        assertEquals(TherapistStatus.INACTIVE, response.getData().getStatus());
    }

    @Test
    void getTherapistById_notFound_throws() {
        when(therapistRepository.findByIdAndDeletedFalse(therapistId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> therapistService.getTherapistById(therapistId));
    }

    @Test
    void getTherapistsByTherapyIds_empty_throws() {
        assertThrows(BadRequestException.class, () -> therapistService.getTherapistsByTherapyIds(List.of()));
    }

    @Test
    void updateTherapist_updatesNameAndTherapies() {
        UpdateTherapistRequest request = new UpdateTherapistRequest();
        request.setName("Updated");
        request.setAssignedTherapyIds(List.of(therapyId));
        request.setStatus(TherapistStatus.ACTIVE);

        when(therapistRepository.findByIdAndDeletedFalse(therapistId)).thenReturn(Optional.of(therapist));
        when(appointmentServiceClient.getTherapyById(therapyId))
                .thenReturn(ApiResponse.success(TherapyMasterClientResponse.builder()
                        .id(therapyId)
                        .name("Kayakalpa")
                        .build()));
        when(therapistRepository.save(therapist)).thenReturn(therapist);
        when(appointmentServiceClient.getAllTherapies())
                .thenReturn(ApiResponse.success(List.of(TherapyMasterClientResponse.builder()
                        .id(therapyId)
                        .name("Kayakalpa")
                        .build())));

        ApiResponse<TherapistResponse> response = therapistService.updateTherapist(therapistId, request);

        assertEquals("Updated", response.getData().getName());
        assertNotNull(response.getData().getAssignedTherapies());
    }

    @Test
    void deleteTherapist_softDeletes() {
        when(therapistRepository.findByIdAndDeletedFalse(therapistId)).thenReturn(Optional.of(therapist));
        when(therapistRepository.save(therapist)).thenReturn(therapist);

        ApiResponse<Void> response = therapistService.deleteTherapist(therapistId);

        assertTrue(response.isSuccess());
        assertEquals(Boolean.TRUE, therapist.getDeleted());
    }

}
