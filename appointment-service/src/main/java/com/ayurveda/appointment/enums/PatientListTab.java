package com.ayurveda.appointment.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * UI tabs for the patients list screen.
 *
 * <p>ACTIVE = booking status is not CANCELLED/COMPLETED, OR CANCELLED/COMPLETED with a follow-up
 * linked via {@code sourceBookingId}.
 *
 * <p>INACTIVE = CANCELLED or COMPLETED with no follow-up for that booking.
 */
public enum PatientListTab {

    ACTIVE,
    INACTIVE;

    /** CANCELLED and COMPLETED — need follow-up check for active/inactive. */
    public static Set<BookingStatus> closedStatuses() {
        return EnumSet.of(BookingStatus.COMPLETED, BookingStatus.CANCELLED);
    }

    /** All statuses except CANCELLED and COMPLETED. */
    public static Set<BookingStatus> openStatuses() {
        return EnumSet.complementOf(EnumSet.copyOf(closedStatuses()));
    }

    /**
     * Statuses loaded from DB before follow-up filtering.
     * ACTIVE loads all; INACTIVE loads only closed statuses.
     */
    public Set<BookingStatus> getQueryStatuses() {
        return switch (this) {
            case ACTIVE -> EnumSet.allOf(BookingStatus.class);
            case INACTIVE -> EnumSet.copyOf(closedStatuses());
        };
    }

}
