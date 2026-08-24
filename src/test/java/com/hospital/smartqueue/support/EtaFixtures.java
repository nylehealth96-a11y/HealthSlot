package com.hospital.smartqueue.support;

import java.util.Set;
import java.util.UUID;

/**
 * Deterministic test data and a test-only trusted staff context for ETA tests.
 *
 * <p>This class is deliberately located in test sources. It is not an authentication mechanism and
 * must never be wired into production request handling.</p>
 */
public final class EtaFixtures {

    public static final UUID HOSPITAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000801");
    public static final UUID BRANCH_ID = UUID.fromString("00000000-0000-0000-0000-000000000802");
    public static final UUID DOCTOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000803");
    public static final UUID STAFF_ID = UUID.fromString("00000000-0000-0000-0000-000000000804");

    private EtaFixtures() {
    }

    public static TestOnlyEtaStaffContext trustedReceptionStaff() {
        return new TestOnlyEtaStaffContext(
                STAFF_ID,
                "RECEPTION",
                HOSPITAL_ID,
                BRANCH_ID,
                Set.of(HOSPITAL_ID),
                Set.of(BRANCH_ID));
    }

    /** Test-only identity fixture; production code must use a real server-side identity adapter. */
    public record TestOnlyEtaStaffContext(
            UUID staffId,
            String role,
            UUID hospitalId,
            UUID branchId,
            Set<UUID> authorizedHospitalIds,
            Set<UUID> authorizedBranchIds) {
    }
}
