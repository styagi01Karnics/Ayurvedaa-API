package com.ayurveda.appointment.util;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PatientCodeGenerator {

    private final JdbcTemplate jdbcTemplate;

    public String generatePatientCode() {

        Long nextValue = jdbcTemplate.queryForObject(
                "SELECT nextval('patient_code_seq')",
                Long.class);

        return String.format("PAT%06d", nextValue);
    }
}