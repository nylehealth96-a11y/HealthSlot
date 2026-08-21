package com.hospital.smartqueue.hospital.application;

import com.hospital.smartqueue.common.domain.ConflictException;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.hospital.domain.Hospital;
import com.hospital.smartqueue.hospital.infrastructure.BranchRepository;
import com.hospital.smartqueue.hospital.infrastructure.HospitalRepository;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class HospitalStructureServiceTest {
    @Test
    void rejectsCaseAndWhitespaceVariantOfExistingHospital() {
        HospitalRepository hospitals = mock(HospitalRepository.class);
        when(hospitals.findByCanonicalName("north hospital")).thenReturn(Optional.of(mock(Hospital.class)));
        HospitalStructureService service = new HospitalStructureService(hospitals, mock(BranchRepository.class), mock(AuditService.class));
        assertThatThrownBy(() -> service.createHospital(" North Hospital ")).isInstanceOf(ConflictException.class);
        verify(hospitals, never()).save(any());
    }
}
