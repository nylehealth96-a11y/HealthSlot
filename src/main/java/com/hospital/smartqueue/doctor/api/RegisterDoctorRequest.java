package com.hospital.smartqueue.doctor.api;
import com.hospital.smartqueue.doctor.domain.DoctorStatus;
import jakarta.validation.constraints.*;
import java.util.Set;
import java.util.UUID;
public record RegisterDoctorRequest(@NotBlank @Size(max = 80) String doctorCode, @NotBlank @Size(max = 200) String name, @NotBlank @Size(max = 200) String specialization, @NotBlank @Size(max = 120) String professionalRegistrationNumber, DoctorStatus status, @NotEmpty Set<UUID> departmentIds) { }
