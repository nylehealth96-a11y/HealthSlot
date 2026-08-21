package com.hospital.smartqueue.doctor.application;

import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.department.infrastructure.DepartmentRepository;
import com.hospital.smartqueue.doctor.domain.Doctor;
import com.hospital.smartqueue.doctor.domain.DoctorStatus;
import com.hospital.smartqueue.doctor.infrastructure.DoctorRepository;
import com.hospital.smartqueue.hospital.infrastructure.HospitalRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class DoctorStatusServiceTest {
    @Test
    void sameStatusRequestDoesNotCreateStatusChangeAuditEvent() {
        Doctor doctor = new Doctor(UUID.randomUUID(), "DOC-1", "doc-1", "Dr Ada", "Cardiology", "REG-1", "reg-1", DoctorStatus.ACTIVE, Set.of());
        DoctorRepository doctors = mock(DoctorRepository.class);
        AuditService audit = mock(AuditService.class);
        UUID hospitalId = doctor.getHospitalId();
        when(doctors.findByIdAndHospitalId(any(), eq(hospitalId))).thenReturn(Optional.of(doctor));
        DoctorService service = new DoctorService(mock(HospitalRepository.class), mock(DepartmentRepository.class), doctors, audit);
        Doctor result = service.setStatus(hospitalId, UUID.randomUUID(), DoctorStatus.ACTIVE);
        assertThat(result.getStatus()).isEqualTo(DoctorStatus.ACTIVE);
        verifyNoInteractions(audit);
    }
}
