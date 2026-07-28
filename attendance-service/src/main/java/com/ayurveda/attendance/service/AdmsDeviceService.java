package com.ayurveda.attendance.service;

public interface AdmsDeviceService {

    /**
     * Returns true when the device SN is allowed (or allowlist is empty).
     */
    boolean isDeviceAllowed(String serialNumber);

    /**
     * Builds the plain-text options handshake response for GET /iclock/cdata.
     */
    String buildOptionsResponse(String serialNumber);

    /**
     * Parses and persists ATTLOG punch lines from a device POST body.
     * Non-ATTLOG tables are acknowledged but not stored as punches.
     */
    void processCdataPost(String serialNumber, String table, String rawBody);

}
