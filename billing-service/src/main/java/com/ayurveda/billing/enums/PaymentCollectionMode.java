package com.ayurveda.billing.enums;

/**
 * Invoice collection modes at generate / settle time.
 * QR is the in-clinic POS-style scan-to-pay path (same PayU link as Online).
 */
public enum PaymentCollectionMode {
    CASH,
    ONLINE,
    QR;

    public static PaymentCollectionMode fromRaw(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String normalized = raw.trim().toUpperCase().replace('-', '_').replace(' ', '_');
        return switch (normalized) {
            case "CASH" -> CASH;
            case "ONLINE", "LINK", "PAYMENT_LINK", "PAYU_LINK" -> ONLINE;
            case "QR", "QR_CODE", "QR_MACHINE", "POS_QR", "UPI_QR" -> QR;
            default -> null;
        };
    }
}
