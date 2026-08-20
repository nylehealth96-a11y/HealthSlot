package com.hospital.smartqueue.patient.application;

import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.patient.domain.Gender;
import com.hospital.smartqueue.patient.domain.Patient;
import com.hospital.smartqueue.patient.infrastructure.PatientRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientQueryServiceTest {
    private final PatientRepository patients = mock(PatientRepository.class);
    private final PatientQueryService service = new PatientQueryService(patients);

    @Test
    void retrievesPatientByPatientNumberIgnoringCase() {
        Patient patient = patient();
        when(patients.findByPatientNumberIgnoreCase("pat-a1")).thenReturn(Optional.of(patient));

        assertSame(patient, service.byPatientNumber(" PAT-A1 "));
    }

    @Test
    void missingPatientUsesConsistentNotFoundRule() {
        UUID id = UUID.randomUUID();
        when(patients.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.byId(id));
    }

    private Patient patient() {
        return new Patient(UUID.randomUUID(), "PAT-A1", "Ada", "Lovelace", LocalDate.of(1990, 1, 1),
                Gender.FEMALE, "+919876543210", null, null, null);
    }
}
