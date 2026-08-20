package com.hospital.smartqueue.patient.application;

import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.patient.domain.Patient;
import com.hospital.smartqueue.patient.infrastructure.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PatientProfileService {
    private final PatientRepository patients;
    private final AuditService audit;

    public PatientProfileService(PatientRepository patients, AuditService audit) {
        this.patients = patients;
        this.audit = audit;
    }

    @Transactional
    public Patient update(UUID patientId, PatientProfileCommand command) {
        PatientProfileValidator.validate(command);
        Patient patient = patients.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        patient.updateProfile(required(command.firstName()), required(command.lastName()), command.dateOfBirth(),
                command.gender(), required(command.mobileNumber()), optional(command.email()), optional(command.address()),
                optional(command.emergencyContact()));
        audit.record("PATIENT_PROFILE_UPDATED", "PATIENT", patient.getId(), null, null);
        return patient;
    }

    private String required(String value) {
        return value.trim();
    }

    private String optional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
