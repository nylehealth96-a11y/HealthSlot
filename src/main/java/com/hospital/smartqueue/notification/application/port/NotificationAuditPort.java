package com.hospital.smartqueue.notification.application.port;

import java.util.UUID;

/** PII-minimized audit boundary for notification state changes. */
public interface NotificationAuditPort { void record(UUID notificationId, String action, String safeReasonCode); }
