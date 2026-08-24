package com.hospital.smartqueue.eta.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Immutable, complete ETA publication for one doctor, branch and local service date. */
@Entity
@Table(name = "eta_prediction_versions", uniqueConstraints = @UniqueConstraint(
        name = "uq_eta_prediction_scope_version", columnNames = {"doctor_id", "branch_id", "service_date", "version_number"}))
public class EtaPredictionVersion {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @Column(name = "hospital_id", nullable = false, updatable = false) private UUID hospitalId;
    @Column(name = "branch_id", nullable = false, updatable = false) private UUID branchId;
    @Column(name = "doctor_id", nullable = false, updatable = false) private UUID doctorId;
    @Column(name = "service_date", nullable = false, updatable = false) private LocalDate serviceDate;
    @Column(name = "timing_revision", nullable = false, updatable = false) private long timingRevision;
    @Column(name = "version_number", nullable = false, updatable = false) private long versionNumber;
    @Column(name = "current_delay_seconds", nullable = false, updatable = false) private long currentDelaySeconds;
    @Column(name = "calculated_at", nullable = false, updatable = false) private Instant calculatedAt;

    protected EtaPredictionVersion() { }
    public EtaPredictionVersion(UUID hospitalId, UUID branchId, UUID doctorId, LocalDate serviceDate,
                                long timingRevision, long versionNumber, long currentDelaySeconds, Instant calculatedAt) {
        this.hospitalId = hospitalId; this.branchId = branchId; this.doctorId = doctorId; this.serviceDate = serviceDate;
        this.timingRevision = timingRevision; this.versionNumber = versionNumber;
        this.currentDelaySeconds = currentDelaySeconds; this.calculatedAt = calculatedAt;
    }
    public UUID getId() { return id; }
    public UUID getHospitalId() { return hospitalId; }
    public UUID getBranchId() { return branchId; }
    public UUID getDoctorId() { return doctorId; }
    public LocalDate getServiceDate() { return serviceDate; }
    public long getTimingRevision() { return timingRevision; }
    public long getVersionNumber() { return versionNumber; }
    public long getCurrentDelaySeconds() { return currentDelaySeconds; }
    public Instant getCalculatedAt() { return calculatedAt; }
}
