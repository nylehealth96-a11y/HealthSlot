package com.hospital.smartqueue.patient.application;

import com.hospital.smartqueue.patient.domain.Gender;

import java.time.LocalDate;

public record PatientProfileCommand(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Gender gender,
        String mobileNumber,
        String email,
        String address,
        String emergencyContact) {
}
