package com.hospital.smartqueue.doctor.scheduling.application;

import java.time.Instant;
import java.util.UUID;

public record AvailableSlot(UUID doctorId, UUID branchId, Instant startAt, Instant endAt) { }
