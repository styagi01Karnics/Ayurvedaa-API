package com.ayurveda.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ayurveda.common.tenant.TenantContext;

class BusinessCodeGeneratorTest {

    @BeforeEach
    void setUp() {
        TenantContext.set(UUID.randomUUID(), "GAN-DL", "hosp_gan_dl");
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void next_startsAt00001() {
        assertEquals("GAN-DL-PT-00001", BusinessCodeGenerator.next(BusinessCodeTypes.PATIENT, List.of()));
    }

    @Test
    void next_incrementsFromMax() {
        assertEquals(
                "GAN-DL-DOC-00008",
                BusinessCodeGenerator.next(
                        BusinessCodeTypes.DOCTOR,
                        List.of("GAN-DL-DOC-00001", "GAN-DL-DOC-00007", "OTHER-DOC-00099")));
    }

    @Test
    void requireTenantCode_whenMissing() {
        TenantContext.clear();
        assertThrows(
                IllegalStateException.class,
                () -> BusinessCodeGenerator.next(BusinessCodeTypes.PATIENT, List.of()));
    }

}
