package com.ayurveda.billing.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.ayurveda.billing.enums.InvoiceStatus;

class InvoiceRefundStatusTest {

    @Test
    void fullRefundLeavesUnpaid() {
        BigDecimal total = bd("1000.00");
        BigDecimal paidAfterFullRefund = bd("0.00");
        assertEquals(InvoiceStatus.UNPAID, InvoiceCalculationUtil.resolveStatus(total, paidAfterFullRefund));
        assertEquals(bd("1000.00"), InvoiceCalculationUtil.leftAmount(total, paidAfterFullRefund));
    }

    @Test
    void partialRefundLeavesPartial() {
        BigDecimal total = bd("1000.00");
        BigDecimal paidAfterPartialRefund = bd("400.00");
        assertEquals(InvoiceStatus.PARTIAL, InvoiceCalculationUtil.resolveStatus(total, paidAfterPartialRefund));
        assertEquals(bd("600.00"), InvoiceCalculationUtil.leftAmount(total, paidAfterPartialRefund));
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
