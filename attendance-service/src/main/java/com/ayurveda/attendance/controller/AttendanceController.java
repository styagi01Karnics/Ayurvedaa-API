package com.ayurveda.attendance.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.attendance.service.DeviceAttendanceLogService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/iclock")
@Tag(name = "Attendance Management", description = "Attendance Management APIs")
public class AttendanceController {

	private final DeviceAttendanceLogService deviceAttendanceLogService;

	@PostMapping(value = "/cdata", consumes = MediaType.ALL_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> receiveAttendanceLog(
			@RequestParam(value = "SN", required = false) String serialNumber,
			@RequestParam(value = "table", required = false) String table,
			@RequestBody(required = false) String rawBody) {

		System.out.println("Received Punch from Machine SN: " + serialNumber);

		if (rawBody != null && !rawBody.trim().isEmpty()) {
			// Raw text ko line by line split karein
			String[] lines = rawBody.split("\\r?\\n");

			for (String line : lines) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("SN=")) {
					continue; // Header lines ko skip karein
				}

				// eSSL ADMS data tab-separated ya multiple spaces se split hota hai
				String[] tokens = line.split("\\s+");

				// Standard format: [EmployeeID, Date, Time, PunchType, VerifyMethod]
				if (tokens.length >= 3) {
					String employeeId = tokens[0];
					String punchDate = tokens[1];
					String punchTime = tokens[2];
					String timestamp = punchDate + " " + punchTime;

					System.out.println("Emp ID: " + employeeId + " | Time: " + timestamp);

					deviceAttendanceLogService.savePunchLog(serialNumber, table, employeeId, punchDate, punchTime,
							line);
					// TODO: Dashboard real-time update ke liye Spring WebSocket / SSE trigger
					// karein
				}
			}
		}

		// Machine ko hamesha plain text "OK" aur HTTP 200 return karna compulsory hai
		return new ResponseEntity<>("OK", HttpStatus.OK);
	}

	@GetMapping(value = "/data", produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> handshake() {

		return new ResponseEntity<>("OK", HttpStatus.OK);

	}

}
