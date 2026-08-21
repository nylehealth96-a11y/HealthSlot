package com.hospital.smartqueue.common.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(nullable = false)
    private String action;

    @Column(name = "target_type", nullable = false)
    private String targetType;

    @Column(name = "target_id", nullable = false)
    private UUID targetId;

    @Column(name = "hospital_id")
    private UUID hospitalId;

    @Column(name = "actor_reference")
    private String actorReference;

    @Column(nullable = false)
    private String metadata;

    protected AuditEvent() {
    }

    public AuditEvent(Instant occurredAt, String action, String targetType, UUID targetId, UUID hospitalId,
                      String actorReference, String metadata) {
        this.occurredAt = occurredAt;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
        this.hospitalId = hospitalId;
        this.actorReference = actorReference;
        this.metadata = metadata;
    }
}
