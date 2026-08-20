package com.hospital.smartqueue.department.application;

import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.department.domain.Department;
import com.hospital.smartqueue.department.infrastructure.DepartmentRepository;
import com.hospital.smartqueue.hospital.domain.Branch;
import com.hospital.smartqueue.hospital.infrastructure.BranchRepository;
import com.hospital.smartqueue.hospital.infrastructure.HospitalRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DepartmentServiceTest {
    private final DepartmentRepository departments = mock(DepartmentRepository.class);
    private final HospitalRepository hospitals = mock(HospitalRepository.class);
    private final BranchRepository branches = mock(BranchRepository.class);
    private final AuditService audit = mock(AuditService.class);
    private final DepartmentService service = new DepartmentService(departments, hospitals, branches, audit);

    @Test
    void rejectsBranchOutsideHospitalContext() {
        UUID hospitalId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        when(hospitals.existsById(hospitalId)).thenReturn(true);
        when(branches.findById(branchId)).thenReturn(Optional.of(new Branch(branchId, UUID.randomUUID(), "Other")));

        assertThrows(NotFoundException.class, () -> service.create(hospitalId, branchId, "Cardiology"));
        verifyNoInteractions(departments, audit);
    }

    @Test
    void createsDepartmentWithinBranchHospitalContext() {
        UUID hospitalId = UUID.randomUUID();
        UUID branchId = UUID.randomUUID();
        when(hospitals.existsById(hospitalId)).thenReturn(true);
        when(branches.findById(branchId)).thenReturn(Optional.of(new Branch(branchId, hospitalId, "Main")));
        when(departments.save(any(Department.class))).thenAnswer(i -> i.getArgument(0));

        Department department = service.create(hospitalId, branchId, " Cardiology ");

        assertEquals(branchId, department.getBranchId());
        assertEquals("Cardiology", department.getName());
        verify(audit).record(eq("DEPARTMENT_CREATED"), eq("DEPARTMENT"), eq(department.getId()), eq(hospitalId), isNull());
    }
}
