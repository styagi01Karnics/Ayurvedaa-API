package com.ayurveda.attendance.controller;

import com.ayurveda.attendance.service.AdmsDeviceService;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ZKTeco / eSSL ADMS (iClock) protocol endpoints.
 * Devices push attendance over HTTP; responses must be plain text {@code OK}.
 * Path aliases ({@code .aspx}) cover common eSSL firmware URL variants.
 */
@Slf4j
@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/iclock")
public class AdmsDeviceController {

    private static final String OK = "OK";

    private final AdmsDeviceService admsDeviceService;

    /**
     * Device handshake / option sync. Firmware polls this before pushing logs.
     */
    @GetMapping(value = {"/cdata", "/cdata.aspx"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> cdataGet(
            @RequestParam(value = "SN", required = false) String serialNumber) {

        log.info("ADMS GET /cdata SN={}", serialNumber);

        if (!admsDeviceService.isDeviceAllowed(serialNumber)) {
            log.warn("Rejected ADMS handshake from unauthorized SN={}", serialNumber);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR");
        }

        return ResponseEntity.ok(admsDeviceService.buildOptionsResponse(serialNumber));
    }

    /**
     * Receives ATTLOG (and other) pushes from the biometric device.
     */
    @PostMapping(
            value = {"/cdata", "/cdata.aspx"},
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> cdataPost(
            @RequestParam(value = "SN", required = false) String serialNumber,
            @RequestParam(value = "table", required = false) String table,
            @RequestBody(required = false) String rawBody) {

        log.info("ADMS POST /cdata SN={} table={} bodyLength={}",
                serialNumber, table, rawBody != null ? rawBody.length() : 0);

        if (!admsDeviceService.isDeviceAllowed(serialNumber)) {
            log.warn("Rejected ADMS data push from unauthorized SN={}", serialNumber);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR");
        }

        admsDeviceService.processCdataPost(serialNumber, table, rawBody);
        return ResponseEntity.ok(OK);
    }

    /**
     * Device polls for pending remote commands. Return OK when none are queued.
     */
    @GetMapping(value = {"/getrequest", "/getrequest.aspx"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getRequest(
            @RequestParam(value = "SN", required = false) String serialNumber) {

        log.debug("ADMS GET /getrequest SN={}", serialNumber);

        if (!admsDeviceService.isDeviceAllowed(serialNumber)) {
            log.warn("Rejected ADMS getrequest from unauthorized SN={}", serialNumber);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR");
        }

        return ResponseEntity.ok(OK);
    }

    /**
     * Device reports results of executed remote commands.
     */
    @PostMapping(
            value = {"/devicecmd", "/devicecmd.aspx"},
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deviceCmd(
            @RequestParam(value = "SN", required = false) String serialNumber,
            @RequestBody(required = false) String rawBody) {

        log.info("ADMS POST /devicecmd SN={} body={}", serialNumber, rawBody);

        if (!admsDeviceService.isDeviceAllowed(serialNumber)) {
            log.warn("Rejected ADMS devicecmd from unauthorized SN={}", serialNumber);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR");
        }

        return ResponseEntity.ok(OK);
    }

    /**
     * Legacy handshake alias used by some firmwares.
     */
    @GetMapping(value = "/data", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> legacyHandshake() {
        return ResponseEntity.ok(OK);
    }

}
