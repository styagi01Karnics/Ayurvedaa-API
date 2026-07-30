package com.ayurveda.doctor.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import com.ayurveda.common.exception.ResourceNotFoundException;
import com.ayurveda.doctor.dto.request.CreateDoctorRequest;
import com.ayurveda.doctor.dto.request.UpdateDoctorStatusRequest;
import com.ayurveda.doctor.dto.response.DoctorResponse;
import com.ayurveda.doctor.entity.Doctor;
import com.ayurveda.doctor.enums.DoctorStatus;
import com.ayurveda.doctor.repository.DoctorRepository;
import com.ayurveda.doctor.util.DoctorCodeGenerator;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorCodeGenerator doctorCodeGenerator;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private UUID doctorId;
    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctorId = UUID.randomUUID();
        doctor = Doctor.builder()
                .doctorName("Dr. Sheekha")
                .doctorCode("DOC-0001")
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
        CreateDoctorRequest request = new CreateDoctorRequest();
        request.setName(" Dr. Sheekha ");
        request.setSpecialization(" Panchakarma ");
        request.setConsultationFees(BigDecimal.valueOf(500));
        request.setFollowUpFees(BigDecimal.valueOf(300));
        request.setAvailability(" Mon-Fri ");

        when(doctorCodeGenerator.generate()).thenReturn("DOC-0001");
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(inv -> {
            Doctor saved = inv.getArgument(0);
            ReflectionTestUtils.setField(saved, "id", doctorId);
            return saved;
        });

        ApiResponse<DoctorResponse> response = doctorService.createDoctor(request);

        assertTrue(response.isSuccess());
        assertEquals("Dr. Sheekha", response.getData().getName());
        assertEquals(DoctorStatus.ACTIVE, response.getData().getStatus());
    }

    @Test
    void getDoctorById_notFound_throws() {
        when(doctorRepository.findByIdAndDeletedFalse(doctorId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> doctorService.getDoctorById(doctorId));
    }

    @Test
    void getAllDoctors_mapsList() {
        when(doctorRepository.findAllByDeletedFalse()).thenReturn(List.of(doctor));

        ApiResponse<List<DoctorResponse>> response = doctorService.getAllDoctors();

        assertEquals(1, response.getData().size());
        assertEquals(doctorId, response.getData().get(0).getId());
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
