package com.ayurveda.patient.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ayurveda.common.ApiResponse;
import com.ayurveda.patient.dto.response.DashboardNewPatientsMonthlyResponse;
import com.ayurveda.patient.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(
        name = "Dashboard",
        description = "APIs used on the main Dashboard page (Total Patients chart).")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Validated
public class DashboardController {

    private final PatientService patientService;

    @Operation(
            summary = "Dashboard – new patients by month",
            description = """
                    Counts newly registered patients (mst_patient.created_at) per month
                    for the given year. Used as the "New Patients" series on the
                    Total Patients chart. Defaults to the current year.
                    """)
    @GetMapping("/new-patients-by-month")
    public ResponseEntity<ApiResponse<DashboardNewPatientsMonthlyResponse>> getNewPatientsByMonth(
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(patientService.getDashboardNewPatientsByMonth(year));
    }

}
