package com.hospital.smartqueue.department.application;

import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.department.infrastructure.DepartmentRepository;
import com.hospital.smartqueue.hospital.infrastructure.BranchRepository;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {
    @Test
    void rejectsDepartmentCreationThroughAnUnrelatedHospital() {
        BranchRepository branches = mock(BranchRepository.class);
        DepartmentService service = new DepartmentService(branches, mock(DepartmentRepository.class), mock(AuditService.class));
        assertThatThrownBy(() -> service.create(UUID.randomUUID(), UUID.randomUUID(), "Cardiology")).isInstanceOf(NotFoundException.class);
    }
}
