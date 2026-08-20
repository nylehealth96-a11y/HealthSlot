package com.hospital.smartqueue.patient.application;

import com.hospital.smartqueue.common.domain.DomainException;

import java.time.LocalDate;
import java.util.regex.Pattern;

final class PatientProfileValidator {
    private static final Pattern MOBILE = Pattern.compile("^\\+?[1-9][0-9]{7,14}$");

    private PatientProfileValidator() {
    }

    static void validate(PatientProfileCommand command) {
        if (command == null || blank(command.firstName()) || blank(command.lastName()) || blank(command.mobileNumber())
                || command.dateOfBirth() == null || command.gender() == null) {
            throw new DomainException("Patient first name, last name, date of birth, gender, and mobile number are required");
        }
        if (!command.dateOfBirth().isBefore(LocalDate.now())) {
            throw new DomainException("Date of birth must be in the past");
        }
        if (!MOBILE.matcher(command.mobileNumber().trim()).matches()) {
            throw new DomainException("Mobile number must be a valid international mobile number");
        }
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
