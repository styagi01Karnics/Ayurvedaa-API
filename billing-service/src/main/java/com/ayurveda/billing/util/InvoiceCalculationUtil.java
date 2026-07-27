package com.ayurveda.billing.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.ayurveda.billing.enums.InvoiceStatus;

public final class InvoiceCalculationUtil {

    private InvoiceCalculationUtil() {
    }

    public static BigDecimal money(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal percentOf(BigDecimal base, BigDecimal percent) {
        if (base == null || percent == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return base.multiply(percent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public static InvoiceStatus resolveStatus(BigDecimal totalAmount, BigDecimal paidAmount) {
        BigDecimal total = money(totalAmount);
        BigDecimal paid = money(paidAmount);

        if (paid.compareTo(BigDecimal.ZERO) <= 0) {
            return InvoiceStatus.UNPAID;
        }
        if (paid.compareTo(total) >= 0) {
            return InvoiceStatus.COMPLETED;
        }
        return InvoiceStatus.ONGOING;
    }

    public static BigDecimal leftAmount(BigDecimal totalAmount, BigDecimal paidAmount) {
        BigDecimal left = money(totalAmount).subtract(money(paidAmount));
        return left.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : left;
    }

}
