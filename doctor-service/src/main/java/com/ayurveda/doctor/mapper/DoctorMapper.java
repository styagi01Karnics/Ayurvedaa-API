package com.ayurveda.doctor.mapper;

import com.ayurveda.doctor.dto.response.DoctorResponse;
import com.ayurveda.doctor.entity.Doctor;

public final class DoctorMapper {

    private DoctorMapper() {
    }

    public static DoctorResponse toResponse(Doctor doctor) {
        return toResponse(doctor, null);
    }

    public static DoctorResponse toResponse(Doctor doctor, Integer serialNo) {
        return DoctorResponse.builder()
                .serialNo(serialNo)
                .id(doctor.getId())
                .name(doctor.getDoctorName())
                .doctorName(doctor.getDoctorName())
                .doctorCode(doctor.getDoctorCode())
                .specialization(doctor.getSpecialization())
                .status(doctor.getStatus())
                .consultationFees(doctor.getConsultationFees())
                .followUpFees(doctor.getFollowUpFees())
                .availability(doctor.getAvailability())
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
