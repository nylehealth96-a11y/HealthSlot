package com.hospital.smartqueue.patient.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "patients")
public class Patient {
    @Id
    private UUID id;
    @Column(name = "patient_number", nullable = false, updatable = false)
    private String patientNumber;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;
    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;
    private String email;
    private String address;
    @Column(name = "emergency_contact")
    private String emergencyContact;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Patient() {
    }

    public Patient(UUID id, String patientNumber, String firstName, String lastName, LocalDate dateOfBirth,
                   Gender gender, String mobileNumber, String email, String address, String emergencyContact) {
        this.id = id;
        this.patientNumber = patientNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.address = address;
        this.emergencyContact = emergencyContact;
    }

    public void updateProfile(String firstName, String lastName, LocalDate dateOfBirth, Gender gender,
                              String mobileNumber, String email, String address, String emergencyContact) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.address = address;
        this.emergencyContact = emergencyContact;
    }

    public UUID getId() { return id; }
    public String getPatientNumber() { return patientNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public Gender getGender() { return gender; }
    public String getMobileNumber() { return mobileNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getEmergencyContact() { return emergencyContact; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
