package com.ayurveda.doctor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayurveda.common.tenant.TenantContext;
import com.ayurveda.doctor.entity.Doctor;
import com.ayurveda.doctor.repository.DoctorRepository;

@ExtendWith(MockitoExtension.class)
class DoctorCodeGeneratorTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorCodeGenerator codeGenerator;

    @BeforeEach
    void setUp() {
        TenantContext.set(UUID.randomUUID(), "GAN-DL", "hosp_gan_dl");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void generate_startsAt00001() {
        when(doctorRepository.findByDoctorCodeStartingWith("GAN-DL-DOC-")).thenReturn(List.of());
        assertEquals("GAN-DL-DOC-00001", codeGenerator.generate());
    }

    @Test
    void generate_increments() {
        when(doctorRepository.findByDoctorCodeStartingWith("GAN-DL-DOC-")).thenReturn(List.of(
                Doctor.builder().doctorCode("GAN-DL-DOC-00007").build()));
        assertEquals("GAN-DL-DOC-00008", codeGenerator.generate());
    }

}
