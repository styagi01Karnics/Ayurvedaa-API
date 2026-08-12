package com.ayurveda.billing.constant;

public final class BillingMessages {

    private BillingMessages() {
    }

    public static final String SALES_FETCHED_SUCCESSFULLY = "Sales fetched successfully.";
    public static final String MONTHLY_REVENUE_FETCHED_SUCCESSFULLY =
            "Monthly revenue fetched successfully.";
    public static final String DASHBOARD_BILLING_SUMMARY_FETCHED =
            "Dashboard billing summary fetched successfully.";
    public static final String INVOICE_GENERATED_SUCCESSFULLY = "Invoice generated successfully.";
    public static final String INVOICES_FETCHED_SUCCESSFULLY = "Invoices fetched successfully.";
    public static final String PART_PAYMENT_RECORDED_SUCCESSFULLY =
            "Part payment recorded successfully.";
    public static final String INVOICE_DELETED_SUCCESSFULLY = "Invoice deleted successfully.";

    public static final String INVOICE_ALREADY_FULLY_PAID = "Invoice is already fully paid.";
    public static final String PAYMENT_EXCEEDS_LEFT_AMOUNT =
            "Payment amount cannot exceed left amount of ";
    public static final String INVOICE_SECTION_REQUIRED =
            "Invoice must include at least one section: Service Type, Medicine, or Therapy.";
    public static final String UNABLE_TO_LOAD_MEDICINE_DETAILS =
            "Unable to load medicine details for dropdown selection.";
    public static final String DISCOUNT_GREATER_THAN_BILL_TOTAL =
            "Discount cannot be greater than bill total.";
    public static final String PAYMENT_AMOUNT_MUST_BE_POSITIVE =
            "Payment amount must be greater than 0.";
    public static final String INVOICE_NOT_FOUND_WITH_ID = "Invoice not found with id: ";
    public static final String MEDICINE_NOT_FOUND_FOR_ID = "Medicine not found for id: ";
    public static final String MEDICINE_NOT_FOUND_IN_INVENTORY = "Medicine not found in inventory: ";
    public static final String SELLING_PRICE_NOT_FOUND = "Selling price not found for medicine: ";
    public static final String FAILED_TO_RESTORE_STOCK = "Failed to restore stock for medicine '";
    public static final String UNABLE_TO_UPDATE_MEDICINE_STOCK = "Unable to update medicine stock for '";
    public static final String INSUFFICIENT_STOCK_FOR_MEDICINE_FALLBACK =
            "Insufficient stock for medicine '";

    public static final String INSUFFICIENT_STOCK_PREFIX = "Insufficient stock for '";
    public static final String INSUFFICIENT_STOCK_AVAILABLE_SUFFIX = "'. Available: ";
    public static final String INSUFFICIENT_STOCK_REQUESTED_SUFFIX = ", requested: ";

    public static final String PATIENT_PACKAGE_CREATED =
            "Patient package created successfully.";
    public static final String PATIENT_PACKAGE_UPDATED =
            "Patient package updated successfully.";
    public static final String PATIENT_PACKAGES_FETCHED =
            "Patient packages fetched successfully.";
    public static final String PATIENT_PACKAGE_STATUS_UPDATED =
            "Patient package status updated successfully.";
    public static final String PATIENT_PACKAGE_NOT_FOUND_WITH_ID =
            "Patient package not found with id: ";

    public static final String PACKAGE_MASTER_CREATED =
            "Package master created successfully.";
    public static final String PACKAGE_MASTER_FETCHED =
            "Package master fetched successfully.";
    public static final String PACKAGE_MASTERS_FETCHED =
            "Package masters fetched successfully.";
    public static final String PACKAGE_MASTER_NOT_FOUND =
            "Package master not found.";
    public static final String PACKAGE_MASTER_ALREADY_EXISTS_WITH_NAME =
            "Package master already exists with name: ";

}
