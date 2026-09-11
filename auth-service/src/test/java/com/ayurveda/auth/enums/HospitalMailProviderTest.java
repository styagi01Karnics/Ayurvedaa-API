package com.ayurveda.auth.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HospitalMailProviderTest {

    @Test
    void infersGmailFromGmailDomain() {
        assertEquals(HospitalMailProvider.GMAIL, HospitalMailProvider.fromEmail("clinic@gmail.com"));
        assertEquals(HospitalMailProvider.GMAIL, HospitalMailProvider.fromEmail("Clinic@GoogleMail.com"));
    }

    @Test
    void infersMicrosoftFromOtherDomains() {
        assertEquals(HospitalMailProvider.MICROSOFT, HospitalMailProvider.fromEmail("clinic@outlook.com"));
        assertEquals(HospitalMailProvider.MICROSOFT, HospitalMailProvider.fromEmail("info@hospital.in"));
    }
}
