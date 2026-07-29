package com.ayurveda.doctor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ayurveda.doctor.entity.Doctor;
import com.ayurveda.doctor.repository.DoctorRepository;

@ExtendWith(MockitoExtension.class)
class DoctorCodeGeneratorTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorCodeGenerator codeGenerator;

    @Test
    void generate_startsAt0001() {
        when(doctorRepository.findByDoctorCodeStartingWith("DOC-")).thenReturn(List.of());
        assertEquals("DOC-0001", codeGenerator.generate());
    }

    @Test
    void generate_increments() {
        when(doctorRepository.findByDoctorCodeStartingWith("DOC-")).thenReturn(List.of(
                Doctor.builder().doctorCode("DOC-0007").build()));
        assertEquals("DOC-0008", codeGenerator.generate());
    }

}
