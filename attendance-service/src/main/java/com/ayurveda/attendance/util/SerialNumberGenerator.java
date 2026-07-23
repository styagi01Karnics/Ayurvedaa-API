package com.ayurveda.attendance.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SerialNumberGenerator {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private final AtomicLong sequence = new AtomicLong(1);

    public String generate() {
        return "ATT-" + LocalDate.now().format(DATE_FORMAT) + "-" + String.format("%04d", sequence.getAndIncrement());
    }

}
