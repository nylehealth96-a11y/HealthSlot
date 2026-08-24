package com.hospital.smartqueue.eta.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

/** PII-free row belonging to one immutable ETA prediction version. */
@Entity
@Table(name = "upcoming_appointment_etas", uniqueConstraints = @UniqueConstraint(
        name = "uq_eta_version_appointment", columnNames = {"prediction_version_id", "appointment_id"}))
public class UpcomingAppointmentEta {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "prediction_version_id", nullable = false)
    private EtaPredictionVersion predictionVersion;
    @Column(name = "appointment_id", nullable = false, updatable = false) private UUID appointmentId;
    @Column(name = "scheduled_start", nullable = false, updatable = false) private Instant scheduledStart;
    @Column(name = "predicted_start", nullable = false, updatable = false) private Instant predictedStart;
    @Column(name = "predicted_completion", nullable = false, updatable = false) private Instant predictedCompletion;
    @Column(name = "sequence_number", nullable = false, updatable = false) private int sequenceNumber;
    protected UpcomingAppointmentEta() { }
    public UpcomingAppointmentEta(EtaPredictionVersion predictionVersion, UUID appointmentId, Instant scheduledStart,
                                  Instant predictedStart, Instant predictedCompletion, int sequenceNumber) {
        this.predictionVersion = predictionVersion; this.appointmentId = appointmentId; this.scheduledStart = scheduledStart;
        this.predictedStart = predictedStart; this.predictedCompletion = predictedCompletion; this.sequenceNumber = sequenceNumber;
    }
    public UUID getAppointmentId() { return appointmentId; }
    public Instant getScheduledStart() { return scheduledStart; }
    public Instant getPredictedStart() { return predictedStart; }
    public Instant getPredictedCompletion() { return predictedCompletion; }
    public int getSequenceNumber() { return sequenceNumber; }
}
