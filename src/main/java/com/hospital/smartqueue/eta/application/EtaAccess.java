package com.hospital.smartqueue.eta.application;

import java.util.UUID;

/** Server-side authorization boundary for ETA operations. */
public interface EtaAccess {
    boolean canView(UUID hospitalId, UUID branchId, UUID doctorId);
    String actorReference();
}
