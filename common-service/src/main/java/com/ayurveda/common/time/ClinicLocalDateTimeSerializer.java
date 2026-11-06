package com.ayurveda.common.time;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Writes clinic-local date-times with their UTC offset, e.g. {@code 2026-10-31T07:23:29.273+05:30}.
 */
public class ClinicLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {

    private final ZoneId clinicZone;

    public ClinicLocalDateTimeSerializer(ZoneId clinicZone) {
        this.clinicZone = clinicZone;
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.atZone(clinicZone).toOffsetDateTime().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
    }
}
