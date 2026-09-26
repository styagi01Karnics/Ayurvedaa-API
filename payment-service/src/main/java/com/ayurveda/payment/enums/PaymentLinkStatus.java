package com.ayurveda.payment.enums;

/**
 * Lifecycle of a patient payment link.
 * <ul>
 *   <li>{@code SHARED} — link created / emailed; active until paid or expiry</li>
 *   <li>{@code PAID} — PayU (or equivalent) succeeded for this invoice link</li>
 *   <li>{@code EXPIRED} — unpaid after the 15-minute window</li>
 *   <li>{@code SUPERSEDED} — replaced by a newer link for the same invoice</li>
 *   <li>{@code OPEN} — legacy active status; treated like {@code SHARED}</li>
 * </ul>
 */
public final class PaymentLinkStatus {

    public static final String SHARED = "SHARED";
    public static final String PAID = "PAID";
    public static final String EXPIRED = "EXPIRED";
    public static final String SUPERSEDED = "SUPERSEDED";
    /** @deprecated use {@link #SHARED}; kept for rows created before SHARED existed */
    public static final String OPEN = "OPEN";

    private PaymentLinkStatus() {
    }

    public static boolean isActiveUnpaid(String status) {
        if (status == null || status.isBlank()) {
            return false;
        }
        String s = status.trim().toUpperCase();
        return SHARED.equals(s) || OPEN.equals(s);
    }
}
