package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentSystemicExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentSystemicExaminationResponse;
import com.ayurveda.appointment.entity.AppointmentSystemicExamination;

@Component
public class AppointmentSystemicExaminationMapper {

    public AppointmentSystemicExamination toEntity(
            CreateAppointmentSystemicExaminationRequest request) {

        if (request == null) {
            return null;
        }

        return AppointmentSystemicExamination.builder()
                .patientId(request.getPatientId())
                .cardiovascular(request.getCardiovascular())
                .respiratory(request.getRespiratory())
                .nervous(request.getNervous())
                .abdomenGi(request.getAbdomenGi())
                .locomotor(request.getLocomotor())
                .build();
    }

    public AppointmentSystemicExaminationResponse toResponse(
            AppointmentSystemicExamination entity) {

        if (entity == null) {
            return null;
        }

        return AppointmentSystemicExaminationResponse.builder()
                .id(entity.getId())
                .patientId(entity.getPatientId())
                .cardiovascular(entity.getCardiovascular())
                .respiratory(entity.getRespiratory())
                .nervous(entity.getNervous())
                .abdomenGi(entity.getAbdomenGi())
                .locomotor(entity.getLocomotor())
                .build();
    }

    public void updateEntity(
            AppointmentSystemicExamination entity,
            CreateAppointmentSystemicExaminationRequest request) {

        entity.setCardiovascular(request.getCardiovascular());
        entity.setRespiratory(request.getRespiratory());
        entity.setNervous(request.getNervous());
        entity.setAbdomenGi(request.getAbdomenGi());
        entity.setLocomotor(request.getLocomotor());
    }

}