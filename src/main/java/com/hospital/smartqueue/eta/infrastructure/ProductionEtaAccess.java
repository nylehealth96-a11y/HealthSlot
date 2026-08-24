package com.hospital.smartqueue.eta.infrastructure;

import com.hospital.smartqueue.eta.application.EtaAccess;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Safe default until real server-side authentication is integrated. It never grants access.
 */
@Component
@Profile("!test")
public class ProductionEtaAccess implements EtaAccess {
    @Override public boolean canView(UUID hospitalId, UUID branchId, UUID doctorId) { return false; }
    @Override public String actorReference() { return "UNAUTHENTICATED"; }
}
