package com.ayurveda.attendance.service;

public interface DeviceAttendanceLogService {

    /**
     * Persists a single punch record pushed by a biometric device.
     *
     * @param serialNumber the device serial number (SN query param)
     * @param table        the ADMS table name the record belongs to (table query param)
     * @param employeeId   the employee/PIN token from the raw punch line
     * @param punchDate    the raw date token from the raw punch line
     * @param punchTime    the raw time token from the raw punch line
     * @param rawLine      the full raw punch line, kept for audit purposes
     */
    void savePunchLog(String serialNumber, String table, String employeeId, String punchDate, String punchTime,
            String rawLine);

}
