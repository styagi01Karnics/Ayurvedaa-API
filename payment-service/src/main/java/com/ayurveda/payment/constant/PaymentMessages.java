package com.ayurveda.payment.constant;

public final class PaymentMessages {

    private PaymentMessages() {
    }

    public static final String PAYMENT_INITIATED = "PayU payment initiated successfully.";
    public static final String PAYMENTS_FETCHED = "Payments fetched successfully.";
    public static final String PAYMENT_FETCHED = "Payment fetched successfully.";
    public static final String PAYU_NOT_CONFIGURED =
            "Payment gateway is not configured for this hospital.";
    public static final String PAYMENT_NOT_FOUND = "Payment not found.";
    public static final String PAYMENT_NOT_FOUND_WITH_TXN = "Payment not found for txnid: ";
    public static final String INVALID_PAYU_HASH = "PayU response hash verification failed.";
    public static final String INVALID_CALLBACK_TENANT = "PayU callback is missing a valid hospital schema.";
    public static final String PAYMENT_LINK_CREATED = "Payment link created successfully.";
    public static final String PAYMENT_LINK_FETCHED = "Payment link fetched successfully.";
    public static final String PAYMENT_LINK_EMAILED = "Payment link emailed to the patient.";
    public static final String PAYMENT_LINK_NOT_FOUND = "Payment link not found.";
    public static final String PAYMENT_LINK_EXPIRED = "This payment link has expired.";
    public static final String PAYMENT_LINK_INVALID = "Invalid payment link.";
    public static final String PAYMENT_LINK_ALREADY_PAID = "This payment link has already been paid.";
    public static final String PAYMENT_LINK_AMOUNT_EXCEEDS_LEFT =
            "Amount cannot exceed invoice left amount of ";
    public static final String INVOICE_NOT_FOUND = "Invoice not found.";
    public static final String INVOICE_LOOKUP_FAILED =
            "Unable to load invoice from billing-service.";
    public static final String REFUND_INITIATED = "Refund initiated successfully.";
    public static final String REFUND_NOT_ALLOWED =
            "Only successful or partially refunded PayU payments can be refunded.";
    public static final String REFUND_REQUIRES_MIHPAYID =
            "PayU mihpayid is missing; cannot refund this payment.";
    public static final String REFUND_EXCEEDS_AMOUNT =
            "Refund amount cannot exceed remaining refundable amount of ";
    public static final String NOTHING_TO_REFUND = "Nothing left to refund on this payment.";
    public static final String REFUND_GATEWAY_FAILED = "PayU refund failed: ";
    public static final String UPI_QR_CREATED = "UPI QR created for on-screen scan payment.";
    public static final String UPI_QR_GATEWAY_FAILED = "PayU UPI QR failed: ";
    public static final String UPI_QR_NOT_CONFIGURED =
            "PayU Dynamic QR (DBQR) is not enabled for this merchant. Contact PayU to enable UPI QR.";
}
