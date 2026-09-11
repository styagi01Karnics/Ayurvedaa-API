package com.ayurveda.billing.enums;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class PaymentCollectionModeTest {

    @Test
    void mapsCashOnlineAndQrAliases() {
        assertEquals(PaymentCollectionMode.CASH, PaymentCollectionMode.fromRaw("cash"));
        assertEquals(PaymentCollectionMode.ONLINE, PaymentCollectionMode.fromRaw("ONLINE"));
        assertEquals(PaymentCollectionMode.ONLINE, PaymentCollectionMode.fromRaw("payment-link"));
        assertEquals(PaymentCollectionMode.QR, PaymentCollectionMode.fromRaw("qr"));
        assertEquals(PaymentCollectionMode.QR, PaymentCollectionMode.fromRaw("POS_QR"));
        assertEquals(PaymentCollectionMode.QR, PaymentCollectionMode.fromRaw("QR_MACHINE"));
    }

    @Test
    void rejectsBlankAndUnknown() {
        assertNull(PaymentCollectionMode.fromRaw(null));
        assertNull(PaymentCollectionMode.fromRaw(" "));
        assertNull(PaymentCollectionMode.fromRaw("PAYU"));
        assertNull(PaymentCollectionMode.fromRaw("UPI"));
    }
}
