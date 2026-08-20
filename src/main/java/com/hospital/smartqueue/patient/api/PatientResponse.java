package com.hospital.smartqueue.patient.api;

import com.hospital.smartqueue.patient.domain.Gender;
import com.hospital.smartqueue.patient.domain.Patient;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record PatientResponse(UUID id, String patientNumber, String firstName, String lastName,
                              LocalDate dateOfBirth, Gender gender, String mobileNumber, String email,
                              String address, String emergencyContact, Instant createdAt, Instant updatedAt) {
    public static PatientResponse from(Patient patient) {
        return new PatientResponse(patient.getId(), patient.getPatientNumber(), patient.getFirstName(),
                patient.getLastName(), patient.getDateOfBirth(), patient.getGender(), patient.getMobileNumber(),
                patient.getEmail(), patient.getAddress(), patient.getEmergencyContact(), patient.getCreatedAt(),
                patient.getUpdatedAt());
    }
}
