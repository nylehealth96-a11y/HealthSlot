package com.hospital.smartqueue.doctor.domain;

import com.hospital.smartqueue.department.domain.Department;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity @Table(name = "doctors")
public class Doctor {
    @Id @GeneratedValue @UuidGenerator private UUID id;
    @Column(name = "hospital_id", nullable = false, updatable = false) private UUID hospitalId;
    @Column(name = "doctor_code", nullable = false) private String doctorCode;
    @Column(name = "canonical_doctor_code", nullable = false) private String canonicalDoctorCode;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String specialization;
    @Column(name = "professional_registration_number", nullable = false) private String professionalRegistrationNumber;
    @Column(name = "canonical_professional_registration_number", nullable = false) private String canonicalProfessionalRegistrationNumber;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private DoctorStatus status;
    @ManyToMany @JoinTable(name = "doctor_departments", joinColumns = @JoinColumn(name = "doctor_id"), inverseJoinColumns = @JoinColumn(name = "department_id"))
    private Set<Department> departments = new LinkedHashSet<>();
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    protected Doctor() { }
    public Doctor(UUID hospitalId, String doctorCode, String canonicalDoctorCode, String name, String specialization, String registrationNumber, String canonicalRegistrationNumber, DoctorStatus status, Set<Department> departments) {
        this.hospitalId = hospitalId; this.doctorCode = doctorCode; this.canonicalDoctorCode = canonicalDoctorCode; this.name = name; this.specialization = specialization; this.professionalRegistrationNumber = registrationNumber; this.canonicalProfessionalRegistrationNumber = canonicalRegistrationNumber; this.status = status; this.departments = new LinkedHashSet<>(departments);
    }
    @PrePersist void created() { createdAt = Instant.now(); updatedAt = createdAt; }
    @PreUpdate void updated() { updatedAt = Instant.now(); }
    public UUID getId() { return id; } public UUID getHospitalId() { return hospitalId; } public String getDoctorCode() { return doctorCode; } public String getName() { return name; } public String getSpecialization() { return specialization; } public String getProfessionalRegistrationNumber() { return professionalRegistrationNumber; } public DoctorStatus getStatus() { return status; } public Set<Department> getDepartments() { return Set.copyOf(departments); }
    public boolean changeStatus(DoctorStatus requestedStatus) { if (status == requestedStatus) return false; status = requestedStatus; return true; }
}
