package com.hospital.smartqueue.common.security;

import java.util.UUID;

public interface StaffAccessContext {
    StaffIdentity requireAppointmentScheduler(UUID hospitalId, UUID branchId);
}
