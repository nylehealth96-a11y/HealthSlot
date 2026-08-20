package com.hospital.smartqueue.common.infrastructure;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    long countByActionAndTargetId(String action, UUID targetId);
}
