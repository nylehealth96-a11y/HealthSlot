package com.hospital.smartqueue.support;

import java.util.Set;
import java.util.UUID;

/** Deterministic identifiers and trusted test-only contexts for consultation tests. */
public final class ConsultationFixtures {
    public static final UUID HOSPITAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000101");
    public static final UUID BRANCH_ID = UUID.fromString("00000000-0000-0000-0000-000000000102");
    public static final UUID RECEPTION_STAFF_ID = UUID.fromString("00000000-0000-0000-0000-000000000103");
    public static final UUID DOCTOR_STAFF_ID = UUID.fromString("00000000-0000-0000-0000-000000000104");
    public static final UUID ASSIGNED_DOCTOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000105");

    private ConsultationFixtures() {
    }

    public static TestOnlyTrustedStaffContext receptionContext() {
        return new TestOnlyTrustedStaffContext(
                RECEPTION_STAFF_ID,
                TestOnlyTrustedStaffContext.StaffRole.RECEPTION,
                HOSPITAL_ID,
                BRANCH_ID,
                Set.of(BRANCH_ID),
                Set.of());
    }

    public static TestOnlyTrustedStaffContext assignedDoctorContext() {
        return new TestOnlyTrustedStaffContext(
                DOCTOR_STAFF_ID,
                TestOnlyTrustedStaffContext.StaffRole.DOCTOR,
                HOSPITAL_ID,
                BRANCH_ID,
                Set.of(BRANCH_ID),
                Set.of(ASSIGNED_DOCTOR_ID));
    }
}
