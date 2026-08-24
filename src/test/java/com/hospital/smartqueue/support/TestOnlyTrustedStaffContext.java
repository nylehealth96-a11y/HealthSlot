package com.hospital.smartqueue.support;

import java.util.Set;
import java.util.UUID;

/**
 * Test fixture only. This is not production authentication and must never be
 * wired into a production request path.
 */
public record TestOnlyTrustedStaffContext(
        UUID staffId,
        StaffRole role,
        UUID hospitalId,
        UUID branchId,
        Set<UUID> authorizedBranchIds,
        Set<UUID> assignedDoctorIds) {

    public TestOnlyTrustedStaffContext {
        authorizedBranchIds = Set.copyOf(authorizedBranchIds);
        assignedDoctorIds = Set.copyOf(assignedDoctorIds);
    }

    public enum StaffRole {
        RECEPTION,
        DOCTOR
    }
}
