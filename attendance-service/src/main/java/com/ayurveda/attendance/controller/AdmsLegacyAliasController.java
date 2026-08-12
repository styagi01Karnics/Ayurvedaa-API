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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Root-level ADMS path aliases used by some eSSL firmwares
 * that call {@code /cdata.aspx} instead of {@code /iclock/cdata}.
 */
@Slf4j
@Hidden
@RestController
@RequiredArgsConstructor
public class AdmsLegacyAliasController {

    private static final String OK = "OK";

    private final AdmsDeviceService admsDeviceService;

    /** Handles legacy GET /cdata handshake from eSSL devices. */
    @GetMapping(value = {"/cdata.aspx", "/cdata"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> cdataGet(
            @RequestParam(value = "SN", required = false) String serialNumber) {

        log.info("ADMS legacy GET /cdata SN={}", serialNumber);

        if (!admsDeviceService.isDeviceAllowed(serialNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR");
        }

        return ResponseEntity.ok(admsDeviceService.buildOptionsResponse(serialNumber));
    }

    /** Handles legacy POST /cdata punch uploads from eSSL devices. */
    @PostMapping(
            value = {"/cdata.aspx", "/cdata"},
            consumes = MediaType.ALL_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> cdataPost(
            @RequestParam(value = "SN", required = false) String serialNumber,
            @RequestParam(value = "table", required = false) String table,
            @RequestBody(required = false) String rawBody) {

        log.info("ADMS legacy POST /cdata SN={} table={}", serialNumber, table);

        if (!admsDeviceService.isDeviceAllowed(serialNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR");
        }

        admsDeviceService.processCdataPost(serialNumber, table, rawBody);
        return ResponseEntity.ok(OK);
    }

    /** Acknowledges legacy GET /getrequest polls from eSSL devices. */
    @GetMapping(value = {"/getrequest.aspx", "/getrequest"}, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getRequest(
            @RequestParam(value = "SN", required = false) String serialNumber) {

        if (!admsDeviceService.isDeviceAllowed(serialNumber)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("ERROR");
        }

        return ResponseEntity.ok(OK);
    }

}
