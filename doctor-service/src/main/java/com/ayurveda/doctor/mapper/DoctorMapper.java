package com.ayurveda.doctor.mapper;

import com.ayurveda.doctor.dto.response.DoctorResponse;
import com.ayurveda.doctor.entity.Doctor;

public final class DoctorMapper {

    private DoctorMapper() {
    }

    public static DoctorResponse toResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .doctorName(doctor.getDoctorName())
                .doctorCode(doctor.getDoctorCode())
                .specialization(doctor.getSpecialization())
                .mobileNumber(doctor.getMobileNumber())
                .email(doctor.getEmail())
                .qualification(doctor.getQualification())
                .department(doctor.getDepartment())
                .consultationRoom(doctor.getConsultationRoom())
                .active(doctor.getActive())
                .createdAt(doctor.getCreatedAt())
                .updatedAt(doctor.getUpdatedAt())
                .build();
    }

}
