package com.ayurveda.appointment.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * UI tabs for the patients list screen.
 * Active = upcoming / open bookings; Inactive = finished / cancelled.
 */
public enum PatientListTab {

    ACTIVE(EnumSet.of(BookingStatus.SCHEDULED, BookingStatus.RESCHEDULED)),
    INACTIVE(EnumSet.of(BookingStatus.COMPLETED, BookingStatus.CANCELLED));

    private final Set<BookingStatus> bookingStatuses;

    PatientListTab(Set<BookingStatus> bookingStatuses) {
        this.bookingStatuses = bookingStatuses;
    }

    public Set<BookingStatus> getBookingStatuses() {
        return bookingStatuses;
    }

}
