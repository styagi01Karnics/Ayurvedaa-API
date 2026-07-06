package com.ayurveda.therapist.controller;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.therapist.dto.request.CreateTherapistRequest;
import com.ayurveda.therapist.dto.response.TherapistResponse;
import com.ayurveda.therapist.service.TherapistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Therapist", description = "Therapist master APIs")
@RestController
@RequestMapping("/api/v1/therapists")
@RequiredArgsConstructor
@Validated
public class TherapistController {

    private final TherapistService therapistService;

    @PostMapping
    public ResponseEntity<ApiResponse<TherapistResponse>> createTherapist(
            @Valid @RequestBody CreateTherapistRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(therapistService.createTherapist(request));
    }

    @GetMapping("/{therapistId}")
    public ResponseEntity<ApiResponse<TherapistResponse>> getTherapistById(@PathVariable UUID therapistId) {
        return ResponseEntity.ok(therapistService.getTherapistById(therapistId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TherapistResponse>>> getAllTherapists() {
        return ResponseEntity.ok(therapistService.getAllTherapists());
    }

}
