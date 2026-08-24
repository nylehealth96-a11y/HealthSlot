package com.hospital.smartqueue.eta.application.port;

import java.time.ZoneId;
import java.util.UUID;

/** Intentional branch-timezone contract; server default timezone must not be used. */
public interface BranchTimezonePort {
    ZoneId timezoneFor(UUID hospitalId, UUID branchId);
}
