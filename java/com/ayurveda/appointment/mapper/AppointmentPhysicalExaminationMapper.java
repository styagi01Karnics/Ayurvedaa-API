package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.CreateAppointmentPhysicalExaminationRequest;
import com.ayurveda.appointment.dto.response.AppointmentPhysicalExaminationResponse;
import com.ayurveda.appointment.entity.AppointmentPhysicalExamination;

@Component
public class AppointmentPhysicalExaminationMapper {

    public AppointmentPhysicalExamination toEntity(
            CreateAppointmentPhysicalExaminationRequest request) {

        if (request == null) {
            return null;
        }

        return AppointmentPhysicalExamination.builder()
                .bookingId(request.getBookingId())
                .weight(request.getWeight())
                .height(request.getHeight())
                .ibw(request.getIbw())
                .pulse(request.getPulse())
                .bp(request.getBp())
                .temperature(request.getTemperature())
                .pallor(request.getPallor())
                .icterus(request.getIcterus())
                .cyanosis(request.getCyanosis())
                .lymphNodes(request.getLymphNodes())
                .oedema(request.getOedema())
                .sensorium(request.getSensorium())
                .acidityGas(request.getAcidityGas())
                .motion(request.getMotion())
                .micturition(request.getMicturition())
                .build();
    }

    public AppointmentPhysicalExaminationResponse toResponse(
            AppointmentPhysicalExamination entity) {

        if (entity == null) {
            return null;
        }

        return AppointmentPhysicalExaminationResponse.builder()
                .id(entity.getId())
                .bookingId(entity.getBookingId())
                .weight(entity.getWeight())
                .height(entity.getHeight())
                .ibw(entity.getIbw())
                .pulse(entity.getPulse())
                .bp(entity.getBp())
                .temperature(entity.getTemperature())
                .pallor(entity.getPallor())
                .icterus(entity.getIcterus())
                .cyanosis(entity.getCyanosis())
                .lymphNodes(entity.getLymphNodes())
                .oedema(entity.getOedema())
                .sensorium(entity.getSensorium())
                .acidityGas(entity.getAcidityGas())
                .motion(entity.getMotion())
                .micturition(entity.getMicturition())
                .build();
    }

    public void updateEntity(
            AppointmentPhysicalExamination entity,
            CreateAppointmentPhysicalExaminationRequest request) {

        entity.setWeight(request.getWeight());
        entity.setHeight(request.getHeight());
        entity.setIbw(request.getIbw());
        entity.setPulse(request.getPulse());
        entity.setBp(request.getBp());
        entity.setTemperature(request.getTemperature());
        entity.setPallor(request.getPallor());
        entity.setIcterus(request.getIcterus());
        entity.setCyanosis(request.getCyanosis());
        entity.setLymphNodes(request.getLymphNodes());
        entity.setOedema(request.getOedema());
        entity.setSensorium(request.getSensorium());
        entity.setAcidityGas(request.getAcidityGas());
        entity.setMotion(request.getMotion());
        entity.setMicturition(request.getMicturition());
    }

}