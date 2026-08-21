package com.hospital.smartqueue.common.infrastructure;

import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuditService {
    private static final String SYSTEM_ACTOR = "SYSTEM";

    private final AuditEventRepository auditEventRepository;
    private final Clock clock;

    public AuditService(AuditEventRepository auditEventRepository, Clock clock) {
        this.auditEventRepository = auditEventRepository;
        this.clock = clock;
    }

    public void record(String action, String targetType, UUID targetId, UUID hospitalId, String metadata) {
        auditEventRepository.save(new AuditEvent(
                Instant.now(clock), action, targetType, targetId, hospitalId, SYSTEM_ACTOR, metadata));
    }
}
