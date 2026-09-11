package com.ayurveda.appointment.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.appointment.dto.request.CreateFollowUpRequest;
import com.ayurveda.appointment.dto.request.UpdateFollowUpStatusRequest;
import com.ayurveda.appointment.dto.response.FollowUpResponse;
import com.ayurveda.appointment.service.FollowUpService;
import com.ayurveda.common.ApiResponse;
import com.ayurveda.common.dto.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Follow-up", description = "Patient follow-up APIs")
@RestController
@RequestMapping("/api/v1/follow-ups")
@RequiredArgsConstructor
@Validated
public class FollowUpController {

    private final FollowUpService followUpService;

    @Operation(
            summary = "Create follow-up",
            description = """
                    Creates a follow-up record for a patient.
                    Does not create an appointment booking.
                    Status defaults to UPCOMING.
                    """)
    @PostMapping
    public ResponseEntity<ApiResponse<FollowUpResponse>> createFollowUp(
            @Valid @RequestBody CreateFollowUpRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(followUpService.createFollowUp(request));
    }

    @Operation(
            summary = "List all follow-ups (paginated)",
            description = "Returns non-deleted follow-ups ordered by appointment date. page/size supported.")
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<FollowUpResponse>>> getAllFollowUps(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(followUpService.getAllFollowUps(page, size));
    }

    @Operation(
            summary = "List follow-ups by patient (paginated)",
            description = "Returns non-deleted follow-ups for the given patient id.")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<PagedResponse<FollowUpResponse>>> getFollowUpsByPatientId(
            @PathVariable UUID patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(followUpService.getFollowUpsByPatientId(patientId, page, size));
    }

    @Operation(
            summary = "Change follow-up status",
            description = "Updates status to UPCOMING, MISSED, COMPLETED, or CANCELLED.")
    @PutMapping("/{followUpId}/status")
    public ResponseEntity<ApiResponse<FollowUpResponse>> updateFollowUpStatus(
            @PathVariable UUID followUpId,
            @Valid @RequestBody UpdateFollowUpStatusRequest request) {
        return ResponseEntity.ok(followUpService.updateFollowUpStatus(followUpId, request));
    }

    @Operation(
            summary = "Cancel follow-up",
            description = "Sets follow-up status to CANCELLED. Record remains visible in lists.")
    @PutMapping("/{followUpId}/cancel")
    public ResponseEntity<ApiResponse<FollowUpResponse>> cancelFollowUp(
            @PathVariable UUID followUpId) {
        return ResponseEntity.ok(followUpService.cancelFollowUp(followUpId));
    }

}
