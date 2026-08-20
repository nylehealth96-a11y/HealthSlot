package com.hospital.smartqueue.common.infrastructure;
import org.springframework.stereotype.Service; import java.time.Instant; import java.util.UUID;
@Service public class AuditService { private final AuditEventRepository repository; public AuditService(AuditEventRepository repository){this.repository=repository;} public void record(String action,String targetType,UUID targetId,UUID hospitalId,String actor){repository.save(new AuditEvent(UUID.randomUUID(),Instant.now(),action,targetType,targetId,hospitalId,actor));} }
