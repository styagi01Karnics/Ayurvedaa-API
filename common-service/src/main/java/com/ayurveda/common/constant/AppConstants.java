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
    public static final String PATIENT_NOT_FOUND_WITH_ID = "Patient not found with id: ";
    public static final String PATIENT_EMAIL_ALREADY_EXISTS =
            "Patient with this email already exists.";
    public static final String PATIENT_MOBILE_ALREADY_EXISTS =
            "Patient with this mobile number already exists.";
    public static final String UNABLE_TO_CREATE_PATIENT = "Unable to create patient.";

    // ===========================
    // Doctor
    // ===========================

    public static final String DOCTOR_CREATED_SUCCESSFULLY = "Doctor created successfully.";
    public static final String DOCTOR_FETCHED_SUCCESSFULLY = "Doctor fetched successfully.";
    public static final String DOCTORS_FETCHED_SUCCESSFULLY = "Doctors fetched successfully.";
    public static final String DOCTOR_DELETED_SUCCESSFULLY = "Doctor deleted successfully.";
    public static final String DOCTOR_STATUS_UPDATED_SUCCESSFULLY = "Doctor status updated successfully.";
    public static final String DOCTOR_NOT_FOUND = "Doctor not found.";
    public static final String DOCTOR_NOT_FOUND_WITH_ID = "Doctor not found with id: ";

    // ===========================
    // Therapist
    // ===========================

    public static final String THERAPIST_CREATED_SUCCESSFULLY = "Therapist created successfully.";
    public static final String THERAPIST_UPDATED_SUCCESSFULLY = "Therapist updated successfully.";
    public static final String THERAPIST_FETCHED_SUCCESSFULLY = "Therapist fetched successfully.";
    public static final String THERAPISTS_FETCHED_SUCCESSFULLY = "Therapists fetched successfully.";
    public static final String THERAPISTS_BY_THERAPIES_FETCHED_SUCCESSFULLY =
            "Therapists fetched successfully for selected therapies.";
    public static final String THERAPIST_DELETED_SUCCESSFULLY = "Therapist deleted successfully.";
    public static final String THERAPIST_STATUS_UPDATED_SUCCESSFULLY =
            "Therapist status updated successfully.";
    public static final String THERAPIST_NOT_FOUND = "Therapist not found.";
    public static final String THERAPIST_NOT_FOUND_WITH_ID = "Therapist not found with id: ";
    public static final String THERAPY_IDS_REQUIRED =
            "At least one therapy id is required.";

    public static final String INVALID_THERAPY_ID =
            "Invalid therapy id: ";

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

    public static final String EMPLOYEE_CREATED_SUCCESSFULLY =
            "Employee created successfully.";
    public static final String EMPLOYEE_FETCHED_SUCCESSFULLY =
            "Employee fetched successfully.";
    public static final String EMPLOYEES_FETCHED_SUCCESSFULLY =
            "Employees fetched successfully.";
    public static final String EMPLOYEE_ALREADY_EXISTS =
            "Employee with this employee ID already exists.";
    public static final String EMPLOYEE_NOT_FOUND =
            "Employee not found.";
    public static final String EMPLOYEE_ID_REQUIRED =
            "Employee record ID must not be null.";
    public static final String DAILY_ATTENDANCE_FETCHED_SUCCESSFULLY =
            "Daily employee attendance records fetched successfully.";

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

    public static final String REQUEST_CONFLICTS_WITH_EXISTING_DATA =
            "Request conflicts with existing data.";

    public static final String MALFORMED_REQUEST_BODY =
            "Malformed or unreadable request body.";

    public static final String ENDPOINT_NOT_FOUND =
            "Endpoint not found.";

    public static final String UPLOADED_FILE_EXCEEDS_MAX_SIZE =
            "Uploaded file exceeds the maximum allowed size.";

    public static final String INVALID_MULTIPART_REQUEST =
            "Invalid multipart request.";

    public static final String SOMETHING_WENT_WRONG =
            "Something went wrong.";

    public static final String INVALID_PARAMETER_VALUE =
            "Invalid value '%s' for parameter '%s'. Expected type: %s.";

    public static final String INVALID_ENUM_VALUE =
            "Invalid value '%s' for field '%s'. Accepted values: %s.";

    public static final String AUTHENTICATION_REQUIRED =
            "Authentication required.";

    public static final String ACCESS_DENIED =
            "Access denied.";

    // ===========================
    // Downstream / Feign
    // ===========================

    public static final String DOWNSTREAM_RESOURCE_NOT_FOUND =
            "Requested resource was not found.";

    public static final String INVALID_DOWNSTREAM_REQUEST =
            "Invalid request to downstream service.";

    public static final String DOWNSTREAM_CONFLICT =
            "Conflict reported by downstream service.";

    public static final String DOWNSTREAM_UNAVAILABLE =
            "Downstream service is unavailable. Please try again later.";

    public static final String DOWNSTREAM_REQUEST_FAILED =
            "Downstream service request failed.";

}
