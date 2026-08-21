package com.hospital.smartqueue.doctor.api;
import com.hospital.smartqueue.doctor.domain.DoctorStatus;
import jakarta.validation.constraints.NotNull;
public record DoctorStatusRequest(@NotNull DoctorStatus status) { }
