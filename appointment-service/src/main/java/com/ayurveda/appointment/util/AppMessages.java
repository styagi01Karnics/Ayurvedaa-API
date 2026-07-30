package com.ayurveda.appointment.util;

public final class AppMessages {

    private AppMessages() {
    }

    /* ==========================
       Appointment Booking
       ========================== */

    public static final String BOOKING_CREATED =
            "Appointment booking created successfully.";

    public static final String BOOKING_UPDATED =
            "Appointment booking updated successfully.";

    public static final String BOOKING_DELETED =
            "Appointment booking deleted successfully.";

    public static final String BOOKING_FOUND =
            "Appointment booking fetched successfully.";

    public static final String BOOKINGS_FOUND =
            "Appointment bookings fetched successfully.";

    public static final String BOOKING_NOT_FOUND =
            "Appointment booking not found.";

    public static final String PATIENT_CREATED_AND_APPOINTMENT_BOOKED =
            "Patient created and appointment booked successfully.";

    public static final String PATIENT_LIST_FETCHED =
            "Patient list fetched successfully.";

    public static final String INVALID_PATIENT_LIST_STATUS =
            "Booking status is not valid for the selected patient list tab.";

    public static final String DOCTOR_SLOT_ALREADY_BOOKED =
            "Doctor already has an appointment for this date and slot time.";

    public static final String APPOINTMENT_STATS_FETCHED =
            "Appointment stats fetched successfully.";

    public static final String CANCELLED_APPOINTMENTS_FETCHED =
            "Cancelled appointments fetched successfully.";

    public static final String TODAY_APPOINTMENTS_BY_CONSULTATION_TYPE_FETCHED =
            "Today's appointments fetched successfully for ";

    public static final String DOCTOR_TODAY_APPOINTMENTS_FETCHED =
            "Doctor today's appointments fetched successfully.";

    public static final String DASHBOARD_TODAY_SCHEDULE_FETCHED =
            "Dashboard today's schedule fetched successfully.";

    public static final String APPOINTMENT_RESCHEDULED =
            "Appointment rescheduled successfully.";

    public static final String APPOINTMENT_DELETED =
            "Appointment deleted successfully.";

    public static final String APPOINTMENT_MARKED_IN_CONSULTATION =
            "Appointment marked as in-consultation.";

    public static final String APPOINTMENT_MARKED_COMPLETED =
            "Appointment marked as completed.";

    /* ==========================
       Appointment
       ========================== */

    public static final String APPOINTMENT_SCHEDULED =
            "Appointment scheduled successfully.";

    public static final String APPOINTMENT_COMPLETED =
            "Appointment completed successfully.";

    public static final String APPOINTMENT_CANCELLED =
            "Appointment cancelled successfully.";

    public static final String APPOINTMENT_NOT_FOUND =
            "Appointment not found.";

    public static final String APPOINTMENT_NOT_FOUND_WITH_ID =
            "Appointment not found with id: ";

    public static final String NO_APPOINTMENTS_FOR_PATIENT =
            "No appointments found for patient id: ";

    public static final String NO_APPOINTMENTS_FOR_STATUS =
            "No appointments found for status: ";

    public static final String NO_APPOINTMENTS_FOR_DATE =
            "No appointments found for date: ";

    public static final String APPOINTMENT_CANNOT_RESCHEDULE_FROM_STATUS =
            "Appointment cannot be rescheduled from status: ";

    public static final String PATIENT_ID_MISMATCH =
            "Patient id does not match this appointment.";

    public static final String APPOINTMENT_IN_CONSULTATION_INVALID_STATUS =
            "Appointment can move to IN_CONSULTATION only from SCHEDULED or RESCHEDULED. Current status: ";

    public static final String APPOINTMENT_COMPLETED_INVALID_STATUS =
            "Appointment can move to COMPLETED only from IN_CONSULTATION. Current status: ";

    public static final String APPOINTMENT_ALREADY_CANCELLED =
            "Appointment is already cancelled.";

    public static final String COMPLETED_APPOINTMENTS_CANNOT_BE_CANCELLED =
            "Completed appointments cannot be cancelled.";

    /* ==========================
       Therapy
       ========================== */

    public static final String THERAPY_CREATED =
            "Therapy created successfully.";

    public static final String THERAPY_DELETED =
            "Therapy deleted successfully.";

    public static final String THERAPY_STATUS_UPDATED =
            "Therapy status updated successfully.";

    public static final String THERAPY_UPDATED =
            "Therapy updated successfully.";

    public static final String TREATMENT_CATEGORY_NOT_FOUND_WITH_ID =
            "Treatment category not found with id: ";

    public static final String THERAPY_ALREADY_EXISTS_WITH_NAME =
            "Therapy already exists with name: ";

    public static final String THERAPY_NOT_FOUND_WITH_ID =
            "Therapy not found with id: ";

    /* ==========================
       Documents
       ========================== */

    public static final String DOCUMENT_FILE_REQUIRED =
            "Document file is required.";

    public static final String FAILED_TO_UPLOAD_DOCUMENT =
            "Failed to upload document.";

    public static final String UNABLE_TO_UPLOAD_DOCUMENT_SERVICE_UNAVAILABLE =
            "Unable to upload document. File service is unavailable.";

    /* ==========================
       Validation
       ========================== */

    public static final String INVALID_REQUEST =
            "Invalid request.";

    public static final String INVALID_BOOKING_STATUS =
            "Invalid booking status.";

    public static final String INVALID_APPOINTMENT_STATUS =
            "Invalid appointment status.";

}
