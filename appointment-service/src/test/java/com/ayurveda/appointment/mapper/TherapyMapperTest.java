package com.ayurveda.appointment.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.ayurveda.appointment.dto.request.CreateTherapyRequest;
import com.ayurveda.appointment.dto.response.TherapyResponse;
import com.ayurveda.appointment.entity.TherapyMaster;
import com.ayurveda.appointment.enums.TherapyMasterStatus;

class TherapyMapperTest {

    private final TherapyMapper mapper = new TherapyMapper();

    @Test
    void toEntity_andToResponse_mapFields() {
        UUID categoryId = UUID.randomUUID();
        CreateTherapyRequest request = CreateTherapyRequest.builder()
                .name(" Podikizhi ")
                .categoryId(categoryId)
                .durationMinutes(45)
                .price(BigDecimal.valueOf(1800))
                .description("Herbal")
                .build();

        TherapyMaster entity = mapper.toEntity(request, TherapyMasterStatus.ACTIVE);

        assertEquals("Podikizhi", entity.getTherapyName());
        assertEquals(TherapyMasterStatus.ACTIVE, entity.getStatus());

        TherapyResponse response = mapper.toResponse(entity, "Massage");
        assertEquals("Podikizhi", response.getName());
        assertEquals("Massage", response.getCategoryName());
    }

    @Test
    void nullSafe() {
        assertNull(mapper.toEntity(null, TherapyMasterStatus.ACTIVE));
        assertNull(mapper.toResponse(null));
    }

}
