package com.hospital.smartqueue.patient.application;

import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.patient.domain.Patient;
import com.hospital.smartqueue.patient.infrastructure.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.UUID;

@Service
public class PatientRegistrationService {
    private final PatientRepository patients;
    private final AuditService audit;

    public PatientRegistrationService(PatientRepository patients, AuditService audit) {
        this.patients = patients;
        this.audit = audit;
    }

    @Transactional
    public Patient register(PatientProfileCommand command) {
        PatientProfileValidator.validate(command);
        UUID id = UUID.randomUUID();
        Patient patient = new Patient(id, patientNumber(id),
                required(command.firstName()), required(command.lastName()), command.dateOfBirth(), command.gender(),
                required(command.mobileNumber()), optional(command.email()), optional(command.address()),
                optional(command.emergencyContact()));
        Patient saved = patients.save(patient);
        audit.record("PATIENT_REGISTERED", "PATIENT", saved.getId(), null, null);
        return saved;
    }

    private String patientNumber(UUID id) {
        return "PAT-" + id.toString().replace("-", "").toUpperCase(Locale.ROOT);
    }

    private String required(String value) {
        return value.trim();
    }

    private String optional(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
