package com.hospital.smartqueue.patient.application;

import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.patient.domain.Gender;
import com.hospital.smartqueue.patient.domain.Patient;
import com.hospital.smartqueue.patient.infrastructure.PatientRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PatientProfileServiceTest {
    private final PatientRepository patients = mock(PatientRepository.class);
    private final AuditService audit = mock(AuditService.class);
    private final PatientProfileService service = new PatientProfileService(patients, audit);

    @Test
    void updatesProfileWithoutChangingIdentifiers() {
        Patient patient = new Patient(UUID.randomUUID(), "PAT-A1", "Ada", "Old", LocalDate.of(1990, 1, 1),
                Gender.FEMALE, "+919876543210", null, null, null);
        when(patients.findById(patient.getId())).thenReturn(Optional.of(patient));

        Patient updated = service.update(patient.getId(), new PatientProfileCommand("Ada", "New",
                LocalDate.of(1990, 1, 1), Gender.FEMALE, "+919876543211", null, null, null));

        assertEquals(patient.getId(), updated.getId());
        assertEquals("PAT-A1", updated.getPatientNumber());
        assertEquals("New", updated.getLastName());
        verify(audit).record(eq("PATIENT_PROFILE_UPDATED"), eq("PATIENT"), eq(patient.getId()), isNull(), isNull());
    }
}
