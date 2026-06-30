package com.ayurveda.doctor.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class DoctorCodeGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final AtomicLong sequence = new AtomicLong(1);

    public String generate() {
        return "DOC-" + LocalDate.now().format(DATE_FORMAT) + "-" + String.format("%04d", sequence.getAndIncrement());
    }

}
