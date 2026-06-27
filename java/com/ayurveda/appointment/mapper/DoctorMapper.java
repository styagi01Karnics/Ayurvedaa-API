package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateDoctorRequest;
import com.ayurveda.appointment.dto.response.DoctorResponse;
import com.ayurveda.appointment.entity.DoctorMaster;

@Component
public class DoctorMapper {

    public DoctorMaster toEntity(CreateDoctorRequest request) {

        if (request == null) {
            return null;
        }

        return DoctorMaster.builder()
                .doctorName(request.getDoctorName())
                .doctorCode(request.getDoctorCode())
                .specialization(request.getSpecialization())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .qualification(request.getQualification())
                .department(request.getDepartment())
                .consultationRoom(request.getConsultationRoom())
                .active(request.getActive())
                .build();
    }

    public DoctorResponse toResponse(DoctorMaster entity) {

        if (entity == null) {
            return null;
        }

        return DoctorResponse.builder()
                .id(entity.getId())
                .doctorName(entity.getDoctorName())
                .doctorCode(entity.getDoctorCode())
                .specialization(entity.getSpecialization())
                .mobileNumber(entity.getMobileNumber())
                .email(entity.getEmail())
                .qualification(entity.getQualification())
                .department(entity.getDepartment())
                .consultationRoom(entity.getConsultationRoom())
                .active(entity.getActive())
                .build();
    }
}