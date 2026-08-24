package com.hospital.smartqueue.support;

import java.util.Set;
import java.util.UUID;

/** Test-only notification identity fixture; it is not production authentication. */
public final class NotificationFixtures {
    public static final UUID HOSPITAL_ID = UUID.fromString("00000000-0000-0000-0000-000000000901");
    public static final UUID BRANCH_ID = UUID.fromString("00000000-0000-0000-0000-000000000902");
    public static final UUID STAFF_ID = UUID.fromString("00000000-0000-0000-0000-000000000903");
    private NotificationFixtures() { }
    public record TestOnlyNotificationStaffContext(UUID staffId, String role, UUID hospitalId, UUID branchId,
                                                    Set<UUID> authorizedHospitalIds, Set<UUID> authorizedBranchIds) { }
    public static TestOnlyNotificationStaffContext receptionStaff() {
        return new TestOnlyNotificationStaffContext(STAFF_ID, "RECEPTION", HOSPITAL_ID, BRANCH_ID,
                Set.of(HOSPITAL_ID), Set.of(BRANCH_ID));
    }
}
