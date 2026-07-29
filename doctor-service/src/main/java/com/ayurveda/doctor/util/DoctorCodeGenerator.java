package com.ayurveda.doctor.util;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.ayurveda.doctor.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DoctorCodeGenerator {

    private static final String PREFIX = "DOC-";
    private static final Pattern DOC_CODE_PATTERN = Pattern.compile("^DOC-\\d{4}$");

    private final DoctorRepository doctorRepository;

    public String generate() {
        int next = doctorRepository.findByDoctorCodeStartingWith(PREFIX).stream()
                .map(doctor -> doctor.getDoctorCode())
                .filter(code -> code != null && DOC_CODE_PATTERN.matcher(code).matches())
                .mapToInt(code -> Integer.parseInt(code.substring(PREFIX.length())))
                .max()
                .orElse(0) + 1;

        return PREFIX + String.format("%04d", next);
    }

}
