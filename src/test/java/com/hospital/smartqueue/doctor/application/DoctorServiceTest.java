package com.hospital.smartqueue.doctor.application;

import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.department.infrastructure.DepartmentRepository;
import com.hospital.smartqueue.doctor.domain.DoctorStatus;
import com.hospital.smartqueue.doctor.infrastructure.DoctorRepository;
import com.hospital.smartqueue.hospital.infrastructure.HospitalRepository;
import org.junit.jupiter.api.Test;
import java.util.Set;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DoctorServiceTest {
    @Test
    void rejectsDepartmentFromAnotherHospitalDuringRegistration() {
        HospitalRepository hospitals = mock(HospitalRepository.class);
        DepartmentRepository departments = mock(DepartmentRepository.class);
        DoctorRepository doctors = mock(DoctorRepository.class);
        UUID hospitalId = UUID.randomUUID();
        when(hospitals.existsById(hospitalId)).thenReturn(true);
        when(doctors.existsByHospitalIdAndCanonicalDoctorCode(any(), any())).thenReturn(false);
        when(doctors.existsByCanonicalProfessionalRegistrationNumber(any())).thenReturn(false);
        DoctorService service = new DoctorService(hospitals, departments, doctors, mock(AuditService.class));
        assertThatThrownBy(() -> service.register(hospitalId, "DOC-1", "Dr Ada", "Cardiology", "REG-1", DoctorStatus.ACTIVE, Set.of(UUID.randomUUID())))
                .isInstanceOf(NotFoundException.class);
        verify(doctors, never()).save(any());
    }
}
