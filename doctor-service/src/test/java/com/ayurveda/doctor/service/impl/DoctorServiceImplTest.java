package com.ayurveda.doctor.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.activity.ActivityLogPublisher;
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.doctor.client.AuthServiceClient;
import com.ayurveda.doctor.dto.client.AuthUserClientRequest;
import com.ayurveda.doctor.dto.client.AuthUserClientResponse;
import com.ayurveda.doctor.dto.client.TenantRoleClientResponse;
import com.ayurveda.doctor.dto.request.CreateDoctorRequest;
import com.ayurveda.doctor.dto.request.UpdateDoctorStatusRequest;
import com.ayurveda.doctor.dto.response.DoctorResponse;
import com.ayurveda.doctor.entity.Doctor;
import com.ayurveda.doctor.enums.DoctorStatus;
import com.ayurveda.doctor.repository.DoctorRepository;
import com.ayurveda.doctor.util.DoctorCodeGenerator;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    private static final UUID DOCTOR_ROLE_ID = UUID.randomUUID();
    private static final UUID AUTH_USER_ID = UUID.randomUUID();

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorCodeGenerator doctorCodeGenerator;

    @Mock
    private ActivityLogPublisher activityLogPublisher;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private UUID doctorId;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        doctor = Doctor.builder()
                .doctorName("Dr. Sheekha")
                .doctorCode("GAN-DL-DOC-00001")
                .specialization("Panchakarma")
                .status(DoctorStatus.ACTIVE)
                .consultationFees(BigDecimal.valueOf(500))
                .followUpFees(BigDecimal.valueOf(300))
                .availability("Mon-Fri")
                .build();
        ReflectionTestUtils.setField(doctor, "id", doctorId);
    }

    @Test
    void createDoctor_defaultsActiveAndSaves() {
        CreateDoctorRequest request = validCreateRequest();

        when(authServiceClient.listRoles()).thenReturn(ApiResponse.success(List.of(doctorRole())));
        when(authServiceClient.registerUser(any())).thenReturn(ApiResponse.success(authUser()));
        when(doctorCodeGenerator.generate()).thenReturn("GAN-DL-DOC-00001");
        when(doctorRepository.saveAndFlush(any(Doctor.class))).thenAnswer(inv -> {
            Doctor saved = inv.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", doctorId);
            return saved;
        });

        ApiResponse<DoctorResponse> response = doctorService.createDoctor(request);

        assertTrue(response.isSuccess());
        assertEquals("Dr. Sheekha", response.getData().getName());
        assertEquals(DoctorStatus.ACTIVE, response.getData().getStatus());

        ArgumentCaptor<AuthUserClientRequest> captor = ArgumentCaptor.forClass(AuthUserClientRequest.class);
        verify(authServiceClient).registerUser(captor.capture());
        assertEquals("9876543210", captor.getValue().getMobileNumber());
        assertEquals(DOCTOR_ROLE_ID, captor.getValue().getTenantRoleId());
        assertEquals("sheekha@gmail.com", captor.getValue().getEmail());
    }

    @Test
    void createDoctor_missingDoctorRole_failsBeforeCreatingLogin() {
        when(authServiceClient.listRoles()).thenReturn(ApiResponse.success(List.of()));

        assertThrows(ResourceNotFoundException.class,
                () -> doctorService.createDoctor(validCreateRequest()));

        verify(authServiceClient, never()).registerUser(any());
        verify(doctorRepository, never()).saveAndFlush(any());
    }

    @Test
    void createDoctor_doctorSaveFails_rollsBackLogin() {
        when(authServiceClient.listRoles()).thenReturn(ApiResponse.success(List.of(doctorRole())));
        when(authServiceClient.registerUser(any())).thenReturn(ApiResponse.success(authUser()));
        when(doctorCodeGenerator.generate()).thenReturn("GAN-DL-DOC-00001");
        when(doctorRepository.saveAndFlush(any(Doctor.class)))
                .thenThrow(new IllegalStateException("db down"));

        assertThrows(IllegalStateException.class,
                () -> doctorService.createDoctor(validCreateRequest()));

        verify(authServiceClient).deleteUser(AUTH_USER_ID);
    }

    private CreateDoctorRequest validCreateRequest() {
        CreateDoctorRequest request = new CreateDoctorRequest();
        request.setName(" Dr. Sheekha ");
        request.setSpecialization(" Panchakarma ");
        request.setConsultationFees(BigDecimal.valueOf(500));
        request.setFollowUpFees(BigDecimal.valueOf(300));
        request.setAvailability(" Mon-Fri ");
        request.setEmail(" Sheekha@Gmail.com ");
        request.setPassword("secret123");
        request.setMobileNumber("9876543210");
        return request;
    }

    private TenantRoleClientResponse doctorRole() {
        TenantRoleClientResponse role = new TenantRoleClientResponse();
        role.setId(DOCTOR_ROLE_ID);
        role.setRoleCode("DOCTOR");
        role.setActive(true);
        return role;
    }

    private AuthUserClientResponse authUser() {
        AuthUserClientResponse user = new AuthUserClientResponse();
        user.setId(AUTH_USER_ID);
        return user;
    }

    @Test
    void getDoctorById_notFound_throws() {
        when(doctorRepository.findByIdAndDeletedFalse(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(doctorId));
    }

    @Test
    void getAllDoctors_mapsList() {
        when(doctorRepository.findAllByDeletedFalse(any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(doctor)));

        ApiResponse<com.ayurveda.common.dto.PagedResponse<DoctorResponse>> response =
                doctorService.getAllDoctors(0, 20);

        assertEquals(1, response.getData().getContent().size());
        assertEquals(doctorId, response.getData().getContent().get(0).getId());
    }

    @Test
    void getActiveDoctors_returnsOnlyActive() {
        when(doctorRepository.findAllByStatusAndDeletedFalse(
                eq(DoctorStatus.ACTIVE), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(doctor)));

        ApiResponse<com.ayurveda.common.dto.PagedResponse<DoctorResponse>> response =
                doctorService.getActiveDoctors(0, 20);

        assertEquals(1, response.getData().getContent().size());
        assertEquals(DoctorStatus.ACTIVE, response.getData().getContent().get(0).getStatus());
    }

    @Test
    void updateDoctorStatus_updatesStatus() {
        UpdateDoctorStatusRequest request = new UpdateDoctorStatusRequest();
        request.setStatus(DoctorStatus.INACTIVE);

        when(doctorRepository.findByIdAndDeletedFalse(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        ApiResponse<DoctorResponse> response = doctorService.updateDoctorStatus(doctorId, request);

        assertEquals(DoctorStatus.INACTIVE, response.getData().getStatus());
    }

    @Test
    void deleteDoctor_softDeletes() {
        when(doctorRepository.findByIdAndDeletedFalse(doctorId)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(doctor)).thenReturn(doctor);

        ApiResponse<Void> response = doctorService.deleteDoctor(doctorId);

        assertTrue(response.isSuccess());
        assertEquals(Boolean.TRUE, doctor.getDeleted());
    }

}
