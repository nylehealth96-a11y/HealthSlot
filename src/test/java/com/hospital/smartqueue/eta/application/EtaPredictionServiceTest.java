package com.hospital.smartqueue.eta.application;

import com.hospital.smartqueue.eta.application.port.AppointmentSchedulePort.ScheduledAppointment;
import com.hospital.smartqueue.eta.application.port.ConsultationTimingPort.ConsultationTiming;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EtaPredictionServiceTest {
    private final Clock clock = Clock.fixed(Instant.parse("2026-08-24T10:25:00Z"), ZoneOffset.UTC);
    private final EtaPredictionService service = new EtaPredictionService(clock);
    private static final Duration FIFTEEN = Duration.ofMinutes(15);

    @Test
    void predictsRahulAmitAndPriyaExample() {
        Instant ten = Instant.parse("2026-08-24T10:00:00Z");
        var result = service.calculate(new ConsultationTiming(UUID.randomUUID(), ten, FIFTEEN, ten, ten.plus(Duration.ofMinutes(25)), 1), List.of(
                appointment("2026-08-24T10:15:00Z"), appointment("2026-08-24T10:30:00Z")));
        assertEquals(Duration.ofMinutes(10), result.currentDelay());
        assertEquals(Instant.parse("2026-08-24T10:25:00Z"), result.appointments().get(0).predictedStart());
        assertEquals(Instant.parse("2026-08-24T10:40:00Z"), result.appointments().get(1).predictedStart());
    }

    @Test
    void idleGapAbsorbsDelayAndNeverMovesEarlierThanSchedule() {
        Instant ten = Instant.parse("2026-08-24T10:00:00Z");
        var result = service.calculate(new ConsultationTiming(UUID.randomUUID(), ten, FIFTEEN, ten, ten.plus(Duration.ofMinutes(25)), 1), List.of(
                appointment("2026-08-24T11:00:00Z")));
        assertEquals(Instant.parse("2026-08-24T11:00:00Z"), result.appointments().get(0).predictedStart());
    }

    private ScheduledAppointment appointment(String scheduledStart) {
        return new ScheduledAppointment(UUID.randomUUID(), Instant.parse(scheduledStart), FIFTEEN, ScheduledAppointment.Status.UPCOMING);
    }
}
