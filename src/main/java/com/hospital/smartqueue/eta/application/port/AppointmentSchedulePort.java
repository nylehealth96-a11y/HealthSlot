package com.hospital.smartqueue.eta.application.port;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Intentional read contract owned by the appointment-scheduling module. */
public interface AppointmentSchedulePort {
    List<ScheduledAppointment> findUpcoming(UUID hospitalId, UUID branchId, UUID doctorId, Instant fromInclusive);

    record ScheduledAppointment(UUID appointmentId, Instant scheduledStart, Duration slotDuration, Status status) {
        public enum Status { UPCOMING, COMPLETED, CANCELLED }
    }
}
