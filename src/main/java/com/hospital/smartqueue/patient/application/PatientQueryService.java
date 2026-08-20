package com.hospital.smartqueue.patient.application;

import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.patient.domain.Patient;
import com.hospital.smartqueue.patient.infrastructure.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Locale;

@Service
public class PatientQueryService {
    private final PatientRepository patients;

    public PatientQueryService(PatientRepository patients) {
        this.patients = patients;
    }

    public Patient byId(UUID id) {
        return patients.findById(id).orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    public Patient byPatientNumber(String patientNumber) {
        return patients.findByPatientNumberIgnoreCase(patientNumber.trim().toLowerCase(Locale.ROOT))
                .orElseThrow(() -> new NotFoundException("Patient not found"));
    }

    public Page<Patient> search(String query, Pageable pageable) {
        return patients.search(query.trim(), pageable);
    }
}
