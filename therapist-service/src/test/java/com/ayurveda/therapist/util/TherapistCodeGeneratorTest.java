package com.ayurveda.therapist.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayurveda.therapist.entity.Therapist;
import com.ayurveda.therapist.repository.TherapistRepository;

@ExtendWith(MockitoExtension.class)
class TherapistCodeGeneratorTest {

    @Mock
    private TherapistRepository therapistRepository;

    @InjectMocks
    private TherapistCodeGenerator codeGenerator;

    @Test
    void generate_startsAt0001WhenEmpty() {
        when(therapistRepository.findByTherapistCodeStartingWith("THP-")).thenReturn(List.of());

        assertEquals("THP-0001", codeGenerator.generate());
    }

    @Test
    void generate_incrementsFromLatest() {
        when(therapistRepository.findByTherapistCodeStartingWith("THP-")).thenReturn(List.of(
                Therapist.builder().therapistCode("THP-0003").build(),
                Therapist.builder().therapistCode("THP-0001").build()));

        assertEquals("THP-0004", codeGenerator.generate());
    }

}
