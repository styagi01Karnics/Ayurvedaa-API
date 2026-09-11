package com.ayurveda.billing.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.ayurveda.billing.enums.InvoiceStatus;

class InvoiceCalculationUtilTest {

    @Test
    void resolveStatusCoversUnpaidPartialCompleted() {
        assertEquals(InvoiceStatus.UNPAID, InvoiceCalculationUtil.resolveStatus(bd("100"), bd("0")));
        assertEquals(InvoiceStatus.PARTIAL, InvoiceCalculationUtil.resolveStatus(bd("100"), bd("40")));
        assertEquals(InvoiceStatus.COMPLETED, InvoiceCalculationUtil.resolveStatus(bd("100"), bd("100")));
        assertEquals(InvoiceStatus.COMPLETED, InvoiceCalculationUtil.resolveStatus(bd("100"), bd("120")));
    }

    @Test
    void leftAmountNeverNegative() {
        assertEquals(bd("60.00"), InvoiceCalculationUtil.leftAmount(bd("100"), bd("40")));
        assertEquals(bd("0.00"), InvoiceCalculationUtil.leftAmount(bd("100"), bd("100")));
        assertEquals(bd("0.00"), InvoiceCalculationUtil.leftAmount(bd("100"), bd("150")));
    }

    private static BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
