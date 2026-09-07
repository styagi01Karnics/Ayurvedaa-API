package com.ayurveda.payment.constant;

public final class PaymentMessages {

    private PaymentMessages() {
    }

    public static final String PAYMENT_INITIATED = "PayU payment initiated successfully.";
    public static final String PAYMENTS_FETCHED = "Payments fetched successfully.";
    public static final String PAYMENT_FETCHED = "Payment fetched successfully.";
    public static final String PAYU_NOT_CONFIGURED =
            "PayU is not configured. Set PAYU_MERCHANT_KEY and PAYU_MERCHANT_SALT.";
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
}
