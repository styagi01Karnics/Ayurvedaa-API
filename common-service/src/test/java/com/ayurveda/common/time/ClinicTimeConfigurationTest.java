package com.ayurveda.common.time;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

class ClinicTimeConfigurationTest {

    @Test
    void localDateTimeIsSerializedWithClinicOffset() throws Exception {
        ObjectMapper mapper = buildMapper();

        String json = mapper.writeValueAsString(Map.of("currentDateTime",
                LocalDateTime.of(2026, 10, 31, 7, 23, 29, 273_589_485)));

        assertEquals("{\"currentDateTime\":\"2026-10-31T07:23:29.273589485+05:30\"}", json);
    }

    @Test
    void localDateTimeWithoutZoneStillDeserializes() throws Exception {
        ObjectMapper mapper = buildMapper();

        LocalDateTime parsed = mapper.readValue("\"2026-10-31T07:23:29\"", LocalDateTime.class);

        assertEquals(LocalDateTime.of(2026, 10, 31, 7, 23, 29), parsed);
    }

    private ObjectMapper buildMapper() {
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        new ClinicTimeConfiguration(ZoneId.of("Asia/Kolkata").getId()).clinicTimeJacksonCustomizer().customize(builder);
        return builder.build();
    }
}
