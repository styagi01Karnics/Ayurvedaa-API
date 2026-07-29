package com.ayurveda.therapist.util;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.ayurveda.therapist.repository.TherapistRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TherapistCodeGenerator {

    private static final String PREFIX = "THP-";
    private static final Pattern THERAPIST_CODE_PATTERN = Pattern.compile("^THP-\\d{4}$");

    private final TherapistRepository therapistRepository;

    public String generate() {
        int next = therapistRepository.findByTherapistCodeStartingWith(PREFIX).stream()
                .map(therapist -> therapist.getTherapistCode())
                .filter(code -> code != null && THERAPIST_CODE_PATTERN.matcher(code).matches())
                .mapToInt(code -> Integer.parseInt(code.substring(PREFIX.length())))
                .max()
                .orElse(0) + 1;

        return PREFIX + String.format("%04d", next);
    }

}
