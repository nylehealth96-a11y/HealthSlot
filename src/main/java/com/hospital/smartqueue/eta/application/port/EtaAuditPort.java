package com.hospital.smartqueue.eta.application.port;

import java.util.UUID;

/** ETA-specific audit contract, invoked in the ETA publication transaction. */
public interface EtaAuditPort {
    void record(EtaAuditEvent event);

    record EtaAuditEvent(UUID hospitalId, UUID branchId, UUID doctorId, long priorVersion,
                         long newVersion, String triggerIdentity) { }
}
