package com.hospital.smartqueue.patient.application;

import com.hospital.smartqueue.common.domain.DomainException;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.patient.domain.Gender;
import com.hospital.smartqueue.patient.domain.Patient;
import com.hospital.smartqueue.patient.infrastructure.PatientRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PatientRegistrationServiceTest {
    private final PatientRepository patients = mock(PatientRepository.class);
    private final AuditService audit = mock(AuditService.class);
    private final PatientRegistrationService service = new PatientRegistrationService(patients, audit);

    @Test
    void registersPatientWithGeneratedNumberAndAuditEvent() {
        when(patients.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient patient = service.register(command());

        assertNotNull(patient.getId());
        assertTrue(patient.getPatientNumber().startsWith("PAT-"));
        assertEquals("Ada", patient.getFirstName());
        verify(audit).record(eq("PATIENT_REGISTERED"), eq("PATIENT"), eq(patient.getId()), isNull(), isNull());
    }

    @Test
    void rejectsFutureDateOfBirthWithoutPersisting() {
        PatientProfileCommand future = new PatientProfileCommand("Ada", "Lovelace", LocalDate.now().plusDays(1),
                Gender.FEMALE, "+919876543210", null, null, null);

        assertThrows(DomainException.class, () -> service.register(future));
        verifyNoInteractions(patients, audit);
    }

    @Test
    void rejectsInvalidMobileWithoutPersisting() {
        PatientProfileCommand invalid = new PatientProfileCommand("Ada", "Lovelace", LocalDate.of(1990, 1, 1),
                Gender.FEMALE, "invalid", null, null, null);

        assertThrows(DomainException.class, () -> service.register(invalid));
        verifyNoInteractions(patients, audit);
    }

    private PatientProfileCommand command() {
        return new PatientProfileCommand(" Ada ", " Lovelace ", LocalDate.of(1990, 1, 1), Gender.FEMALE,
                "+919876543210", "ada@example.test", null, null);
    }
}
