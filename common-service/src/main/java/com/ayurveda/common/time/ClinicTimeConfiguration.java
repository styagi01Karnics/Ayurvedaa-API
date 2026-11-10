package com.ayurveda.common.time;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Keeps every service on one clinic timezone so that {@code LocalDate.now()}, {@code LocalDateTime.now()}
 * and JPA auditing produce clinic-local values, and API date-times carry the clinic UTC offset.
 */
@Configuration
public class ClinicTimeConfiguration {

    private final ZoneId clinicZone;

    public ClinicTimeConfiguration(@Value("${ayurveda.timezone:Asia/Kolkata}") String zoneId) {
        this.clinicZone = ZoneId.of(zoneId);
    }

    @PostConstruct
    void applyClinicDefaultTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone(clinicZone));
    }

    @Bean
    Jackson2ObjectMapperBuilderCustomizer clinicTimeJacksonCustomizer() {
        return builder -> builder
                .timeZone(TimeZone.getTimeZone(clinicZone))
                .serializerByType(LocalDateTime.class, new ClinicLocalDateTimeSerializer(clinicZone));
    }
}
