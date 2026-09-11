package com.ayurveda.billing.enums;

public enum InvoiceStatus {
    UNPAID,
    /** Partial balance remaining after cash or PayU (preferred for new settlements). */
    PARTIAL,
    /** Legacy / synonym for partial payments still present in older rows. */
    ONGOING,
    COMPLETED
}
