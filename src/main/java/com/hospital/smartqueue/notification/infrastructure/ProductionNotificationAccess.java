package com.hospital.smartqueue.notification.infrastructure;

import com.hospital.smartqueue.notification.application.NotificationAccess;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.UUID;

/** Denies all production access until real authenticated staff identity is integrated. */
@Component @Profile("!test")
public class ProductionNotificationAccess implements NotificationAccess {
    @Override public boolean canAccess(UUID hospitalId, UUID branchId, UUID notificationId) { return false; }
    @Override public String actorReference() { return "UNAUTHENTICATED"; }
}
