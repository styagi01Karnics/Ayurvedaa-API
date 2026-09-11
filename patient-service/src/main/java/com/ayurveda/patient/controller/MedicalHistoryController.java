package com.ayurveda.patient.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.patient.dto.response.MedicalHistoryOptionsResponse;
import com.ayurveda.patient.enums.Gender;
import com.ayurveda.patient.service.MedicalHistoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Medical-history option catalog APIs (gender-filtered enums for intake forms).
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/medical-history")
@Tag(name = "Medical History", description = "Medical history option catalog APIs")
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    @Operation(
            summary = "Get medical history options",
            description = """
                    Returns common + gender-specific medical-history options
                    (past conditions, surgeries, medications, allergies).
                    Required query param: gender = MALE | FEMALE | OTHER.
                    FEMALE receives ALL + FEMALE options; MALE receives ALL + MALE;
                    OTHER receives ALL only.
                    """)
    @GetMapping("/options")
    public ResponseEntity<ApiResponse<MedicalHistoryOptionsResponse>> getMedicalHistoryOptions(
            @RequestParam Gender gender) {

        log.info("Received request to fetch medical history options for gender={}", gender);

        return ResponseEntity.ok(medicalHistoryService.getOptions(gender));
    }

}
