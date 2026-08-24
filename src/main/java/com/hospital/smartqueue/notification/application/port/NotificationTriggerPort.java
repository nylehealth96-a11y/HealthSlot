package com.hospital.smartqueue.notification.application.port;

import java.time.Instant;
import java.util.UUID;

/** Server-side workflow trigger boundary; never populated from normal request input. */
public interface NotificationTriggerPort {
    record NotificationTrigger(UUID sourceEventId, long sourceRevision, UUID hospitalId, UUID branchId,
                               UUID appointmentOrVisitId, NotificationType type, RecipientClass recipientClass,
                               Instant scheduledAt, boolean eligible) { }
    enum NotificationType { BOOKED, REMINDER, DOCTOR_DELAYED, NEARLY_DUE, PATIENT_CALLED, CANCELLED, RESCHEDULED }
    enum RecipientClass { PATIENT, RECEPTION }
}
