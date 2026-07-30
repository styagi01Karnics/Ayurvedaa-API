package com.ayurveda.doctor.mapper;

import com.ayurveda.doctor.dto.response.DoctorResponse;
import com.ayurveda.doctor.entity.Doctor;

public final class DoctorMapper {

    private DoctorMapper() {
    }

    public static DoctorResponse toResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .id(doctor.getId())
                .name(doctor.getDoctorName())
                .specialization(doctor.getSpecialization())
                .status(doctor.getStatus())
                .consultationFees(doctor.getConsultationFees())
                .followUpFees(doctor.getFollowUpFees())
                .availability(doctor.getAvailability())
                .build();
    }

}
