package com.ayurveda.appointment.dto.request;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreatePatientClientRequest {

    private String fullName;
    private String gender;
    private java.time.LocalDate dateOfBirth;
    private Integer age;
    private String preferredLanguage;
    private String mobileNumber;
    private String email;
    private String state;
    private String city;
    private String address;
    private String emergencyContactName;
    private String emergencyRelationship;
    private String emergencyPhoneNumber;
    private String idProofType;
    private String idProofNumber;
    private String occupation;
    private String insuranceDetails;

    public static CreatePatientClientRequest from(CreatePatientDetailsRequest request) {
        CreatePatientClientRequest clientRequest = new CreatePatientClientRequest();
        clientRequest.setFullName(request.getFullName());
        clientRequest.setGender(request.getGender());
        clientRequest.setDateOfBirth(request.getDateOfBirth());
        clientRequest.setAge(request.getAge());
        clientRequest.setPreferredLanguage(request.getPreferredLanguage());
        clientRequest.setMobileNumber(request.getMobileNumber());
        clientRequest.setEmail(request.getEmail());
        clientRequest.setState(request.getState());
        clientRequest.setCity(request.getCity());
        clientRequest.setAddress(request.getPermanentAddress());
        clientRequest.setEmergencyContactName(request.getEmergencyContactName());
        clientRequest.setEmergencyRelationship(request.getEmergencyRelationship());
        clientRequest.setEmergencyPhoneNumber(request.getEmergencyPhoneNumber());
        clientRequest.setIdProofType(request.getIdProofType());
        clientRequest.setIdProofNumber(request.getIdProofNumber());
        clientRequest.setOccupation(request.getOccupation());
        clientRequest.setInsuranceDetails(request.getInsuranceDetails());
        return clientRequest;
    }

}
