package com.ayurveda.therapist.util;

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
import com.ayurveda.therapist.entity.Therapist;
import com.ayurveda.therapist.repository.TherapistRepository;

@ExtendWith(MockitoExtension.class)
class TherapistCodeGeneratorTest {

    @Mock
    private TherapistRepository therapistRepository;

    @InjectMocks
    private TherapistCodeGenerator codeGenerator;

    @BeforeEach
    void setUp() {
        TenantContext.set(UUID.randomUUID(), "GAN-DL", "hosp_gan_dl");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void generate_startsAt00001WhenEmpty() {
        when(therapistRepository.findByTherapistCodeStartingWith("GAN-DL-THP-")).thenReturn(List.of());

        assertEquals("GAN-DL-THP-00001", codeGenerator.generate());
    }

    @Test
    void generate_incrementsFromLatest() {
        when(therapistRepository.findByTherapistCodeStartingWith("GAN-DL-THP-")).thenReturn(List.of(
                Therapist.builder().therapistCode("GAN-DL-THP-00003").build(),
                Therapist.builder().therapistCode("GAN-DL-THP-00001").build()));

        assertEquals("GAN-DL-THP-00004", codeGenerator.generate());
    }

}
