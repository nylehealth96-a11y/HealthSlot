package com.hospital.smartqueue.eta.application;

import com.hospital.smartqueue.eta.application.port.AppointmentSchedulePort.ScheduledAppointment;
import com.hospital.smartqueue.eta.application.port.ConsultationTimingPort.ConsultationTiming;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/** Pure deterministic ETA calculation; publication is intentionally separate from upstream reads. */
@Service
public class EtaPredictionService {
    private final Clock clock;

    public EtaPredictionService(Clock clock) { this.clock = clock; }

    public Calculation calculate(ConsultationTiming timing, List<ScheduledAppointment> appointments) {
        if (timing.slotDuration() == null || timing.slotDuration().isNegative() || timing.slotDuration().isZero()) {
            throw new EtaUnavailableException();
        }
        Instant expectedEnd = timing.scheduledStart().plus(timing.slotDuration());
        Instant authoritativeEnd = timing.actualEnd() != null ? timing.actualEnd() : Instant.now(clock);
        Duration delay = Duration.between(expectedEnd, authoritativeEnd);
        if (delay.isNegative()) delay = Duration.ZERO;
        Instant previousCompletion = expectedEnd.plus(delay);
        List<PredictedAppointment> predictions = new ArrayList<>();
        List<ScheduledAppointment> ordered = appointments.stream()
                .filter(a -> a.status() == ScheduledAppointment.Status.UPCOMING)
                .sorted(Comparator.comparing(ScheduledAppointment::scheduledStart).thenComparing(ScheduledAppointment::appointmentId))
                .toList();
        for (ScheduledAppointment appointment : ordered) {
            if (appointment.slotDuration() == null || appointment.slotDuration().isNegative() || appointment.slotDuration().isZero()) {
                throw new EtaUnavailableException();
            }
            Instant predictedStart = appointment.scheduledStart().isAfter(previousCompletion)
                    ? appointment.scheduledStart() : previousCompletion;
            Instant predictedCompletion = predictedStart.plus(appointment.slotDuration());
            predictions.add(new PredictedAppointment(appointment.appointmentId(), appointment.scheduledStart(), predictedStart,
                    predictedCompletion));
            previousCompletion = predictedCompletion;
        }
        return new Calculation(delay, predictions);
    }

    public record Calculation(Duration currentDelay, List<PredictedAppointment> appointments) { }
    public record PredictedAppointment(UUID appointmentId, Instant scheduledStart, Instant predictedStart,
                                       Instant predictedCompletion) { }
}
