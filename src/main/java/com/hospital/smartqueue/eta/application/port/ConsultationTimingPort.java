package com.hospital.smartqueue.eta.application.port;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/** Intentional read contract owned by the consultation module. */
public interface ConsultationTimingPort {
    Optional<ConsultationTiming> currentFor(UUID hospitalId, UUID branchId, UUID doctorId);

    record ConsultationTiming(UUID appointmentId, Instant scheduledStart, Duration slotDuration, Instant actualStart,
                              Instant actualEnd, long revision) { }
}
