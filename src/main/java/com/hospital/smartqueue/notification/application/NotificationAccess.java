package com.hospital.smartqueue.notification.application;

import java.util.UUID;

/** Server-side access boundary for notification operational views. */
public interface NotificationAccess {
    boolean canAccess(UUID hospitalId, UUID branchId, UUID notificationId);
    String actorReference();
}
