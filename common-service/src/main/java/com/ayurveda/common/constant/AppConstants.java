package com.ayurveda.common.constant;

public final class AppConstants {

    private AppConstants() {
    }

    // ===========================
    // Common Success Messages
    // ===========================

    public static final String CREATED_SUCCESSFULLY = "Created successfully.";
    public static final String UPDATED_SUCCESSFULLY = "Updated successfully.";
    public static final String FETCHED_SUCCESSFULLY = "Fetched successfully.";
    public static final String LIST_FETCHED_SUCCESSFULLY = "Records fetched successfully.";
    public static final String DELETED_SUCCESSFULLY = "Deleted successfully.";

    // ===========================
    // Patient
    // ===========================

    public static final String PATIENT_CREATED_SUCCESSFULLY = "Patient created successfully.";
    public static final String PATIENT_UPDATED_SUCCESSFULLY = "Patient updated successfully.";
    public static final String PATIENT_FETCHED_SUCCESSFULLY = "Patient fetched successfully.";
    public static final String PATIENTS_FETCHED_SUCCESSFULLY = "Patients fetched successfully.";
    public static final String PATIENT_DELETED_SUCCESSFULLY = "Patient deleted successfully.";

    public static final String PATIENT_NOT_FOUND = "Patient not found.";
    public static final String PATIENT_EMAIL_ALREADY_EXISTS =
            "Patient with this email already exists.";
    public static final String PATIENT_MOBILE_ALREADY_EXISTS =
            "Patient with this mobile number already exists.";

    // ===========================
    // Attendance
    // ===========================

    public static final String ATTENDANCE_MARKED_SUCCESSFULLY = "Attendance marked successfully.";
    public static final String ATTENDANCE_CHECKED_OUT_SUCCESSFULLY = "Attendance check-out recorded successfully.";
    public static final String ATTENDANCE_STATUS_UPDATED_SUCCESSFULLY = "Attendance status updated successfully.";
    public static final String ATTENDANCE_FETCHED_SUCCESSFULLY = "Attendance fetched successfully.";
    public static final String ATTENDANCES_FETCHED_SUCCESSFULLY = "Attendance records fetched successfully.";
    public static final String ATTENDANCE_DELETED_SUCCESSFULLY = "Attendance deleted successfully.";

    public static final String ATTENDANCE_NOT_FOUND = "Attendance record not found.";
    public static final String ATTENDANCE_ALREADY_MARKED =
            "Attendance already marked for this staff on the given date.";
    public static final String ATTENDANCE_ALREADY_CHECKED_OUT =
            "Attendance for this record has already been checked out.";
    public static final String INVALID_CHECK_OUT_TIME =
            "Check-out time cannot be before check-in time.";
    public static final String ATTENDANCE_DATE_IN_FUTURE =
            "Attendance date cannot be in the future.";
    public static final String EMP_ID_REQUIRED =
            "Employee ID must not be blank.";
    public static final String ATTENDANCE_ID_REQUIRED =
            "Attendance ID must not be null.";

    // ===========================
    // Validation
    // ===========================

    public static final String INVALID_DATE_OF_BIRTH =
            "Date of birth cannot be in the future.";

    public static final String INVALID_EMERGENCY_CONTACT =
            "Emergency contact number cannot be the same as mobile number.";

    public static final String INVALID_AADHAAR =
            "Invalid Aadhaar number.";

    public static final String INVALID_PAN =
            "Invalid PAN number.";

    public static final String INVALID_PASSPORT =
            "Invalid Passport number.";

    public static final String INVALID_DRIVING_LICENSE =
            "Invalid Driving License number.";

    public static final String INVALID_VOTER_ID =
            "Invalid Voter ID.";

    public static final String INVALID_ID_PROOF =
            "Invalid ID proof type.";

    // ===========================
    // Common Exception Messages
    // ===========================

    public static final String BAD_REQUEST =
            "Bad request.";

    public static final String INTERNAL_SERVER_ERROR =
            "Internal server error.";

    public static final String VALIDATION_FAILED =
            "Validation failed.";

    public static final String DUPLICATE_RESOURCE =
            "Resource already exists.";
}