package com.hospital.smartqueue.hospital.application;

import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.hospital.domain.Branch;
import com.hospital.smartqueue.hospital.domain.Hospital;
import com.hospital.smartqueue.hospital.infrastructure.BranchRepository;
import com.hospital.smartqueue.hospital.infrastructure.HospitalRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HospitalStructureServiceTest {
    private final HospitalRepository hospitals = mock(HospitalRepository.class);
    private final BranchRepository branches = mock(BranchRepository.class);
    private final AuditService audit = mock(AuditService.class);
    private final HospitalService service = new HospitalService(hospitals, branches, audit);

    @Test
    void rejectsBlankHospitalName() {
        assertThrows(RuntimeException.class, () -> service.createHospital("  "));
        verifyNoInteractions(hospitals, audit);
    }

    @Test
    void rejectsBranchWhenHospitalDoesNotExist() {
        UUID hospitalId = UUID.randomUUID();
        when(hospitals.existsById(hospitalId)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.createBranch(hospitalId, "Main"));
        verifyNoInteractions(branches, audit);
    }

    @Test
    void createsBranchOnlyWithinExistingHospital() {
        UUID hospitalId = UUID.randomUUID();
        when(hospitals.existsById(hospitalId)).thenReturn(true);
        when(branches.save(any(Branch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Branch branch = service.createBranch(hospitalId, " Main ");

        assertEquals(hospitalId, branch.getHospitalId());
        assertEquals("Main", branch.getName());
        verify(audit).record(eq("BRANCH_CREATED"), eq("BRANCH"), eq(branch.getId()), eq(hospitalId), isNull());
    }

    @Test
    void listsOnlyRequestedHospitalBranches() {
        UUID hospitalId = UUID.randomUUID();
        when(hospitals.existsById(hospitalId)).thenReturn(true);
        when(branches.findByHospitalId(hospitalId)).thenReturn(java.util.List.of(new Branch(UUID.randomUUID(), hospitalId, "Main")));
        assertEquals(1, service.listBranches(hospitalId).size());
        verify(branches).findByHospitalId(hospitalId);
    }
}
