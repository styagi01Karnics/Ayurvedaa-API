package com.ayurveda.appointment.mapper;

import org.springframework.stereotype.Component;

import com.ayurveda.appointment.dto.request.*;
import com.ayurveda.appointment.entity.*;

@Component
public class MedicalAssessmentMapper {
	
	public AppointmentAyurvedicAssessment toEntity(
	        AyurvedicAssessmentRequest request) {

	    if (request == null) {
	        return null;
	    }

	    return AppointmentAyurvedicAssessment.builder()
	            .doshaType(request.getDoshaType())
	            .bodyConstitution(
	            	    request.getBodyConstitution() == null
	            	            ? null
	            	            : String.join(", ", request.getBodyConstitution())
	            	)
	            .currentImbalances(request.getCurrentImbalances())
	            .build();
	}
	
	public AppointmentPhysicalExamination toEntity(
	        PhysicalExaminationRequest request) {

	    if (request == null) {
	        return null;
	    }

	    return AppointmentPhysicalExamination.builder()
	            .weight(request.getWeight())
	            .height(request.getHeight())
	            .ibw(request.getIbw())
//	            .pulse(request.getPulse())
//	            .pulse(request.getPulse() == null ? null : String.valueOf(request.getPulse()))
//	            .bloodPressure(request.getBloodPressure())
	            .bp(request.getBloodPressure())
//	            .temperature(request.getTemperature())
//	            .temperature(request.getTemperature() == null ? null : String.valueOf(request.getTemperature()))
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
	
	public AppointmentMedicalHistory toEntity(
	        MedicalHistoryRequest request) {

	    if (request == null) {
	        return null;
	    }

	    return AppointmentMedicalHistory.builder()
	            .pastMedicalConditions(request.getPastMedicalConditions())
	            .pastSurgeries(request.getPastSurgeries())
	            .currentMedications(request.getCurrentMedications())
	            .allergies(
	            	    request.getAllergies() == null
	            	            ? null
	            	            : String.join(", ", request.getAllergies())
	            	)
	            .familyHistory(request.getFamilyHistory())
	            .build();
	}
	
	public AppointmentLifestyleInformation toEntity(
	        LifestyleInformationRequest request) {

	    if (request == null) {
	        return null;
	    }

	    return AppointmentLifestyleInformation.builder()
	            .dietType(request.getDietType())
	            .sleepPattern(request.getSleepPattern())
	            .exerciseHabits(request.getExerciseHabits())
	            .addiction(request.getAddiction())
	            .build();
	}
	
	public AppointmentSystemicExamination toEntity(
	        SystemicExaminationRequest request) {

	    if (request == null) {
	        return null;
	    }

	    return AppointmentSystemicExamination.builder()
	            .cardiovascular(request.getCardiovascular())
	            .respiratory(request.getRespiratory())
	            .nervous(request.getNervous())
	            .abdomenGi(request.getAbdomenGI())
	            .locomotor(request.getLocomotor())
	            .build();
	}
	
	public AppointmentTreatmentPlan toEntity(
	        TreatmentPlanRequest request) {

	    if (request == null) {
	        return null;
	    }

	    return AppointmentTreatmentPlan.builder()
	    		.investigationAndPlanSuggested(request.getInvestigationSuggested())
	    		.planTaken(request.getPlanTaken())
	            .build();
	}
	
	
}