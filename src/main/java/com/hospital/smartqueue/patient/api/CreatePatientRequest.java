package com.hospital.smartqueue.patient.api;

import com.hospital.smartqueue.patient.application.PatientProfileCommand;
import com.hospital.smartqueue.patient.domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePatientRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotNull @Past LocalDate dateOfBirth,
        @NotNull Gender gender,
        @NotBlank @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "must be a valid international mobile number") String mobileNumber,
        @Email @Size(max = 254) String email,
        @Size(max = 1000) String address,
        @Size(max = 500) String emergencyContact) {
    public PatientProfileCommand toCommand() {
        return new PatientProfileCommand(firstName, lastName, dateOfBirth, gender, mobileNumber, email, address, emergencyContact);
    }
}
