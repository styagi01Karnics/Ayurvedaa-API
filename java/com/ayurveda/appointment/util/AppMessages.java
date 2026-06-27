package com.ayurveda.appointment.util;

public final class AppMessages {

    private AppMessages() {
        // Prevent instantiation
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