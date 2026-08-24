package com.hospital.smartqueue.notification.infrastructure;
import com.hospital.smartqueue.notification.domain.NotificationIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; import java.util.UUID;
public interface NotificationIntentRepository extends JpaRepository<NotificationIntent,UUID>{ Optional<NotificationIntent> findBySourceEventIdAndType(UUID sourceEventId, com.hospital.smartqueue.notification.application.port.NotificationTriggerPort.NotificationType type); }
