package com.hospital.smartqueue.hospital.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hospitals")
public class Hospital {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @Column(nullable = false) private String name;
    @Column(name = "canonical_name", nullable = false) private String canonicalName;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected Hospital() { }
    public Hospital(String name, String canonicalName) { this.name = name; this.canonicalName = canonicalName; }
    @PrePersist void created() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void updated() { updatedAt = Instant.now(); }
    public UUID getId() { return id; }
    public String getName() { return name; }
}
