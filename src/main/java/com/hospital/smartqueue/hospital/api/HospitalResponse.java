package com.hospital.smartqueue.hospital.api;
import com.hospital.smartqueue.hospital.domain.Hospital;
import java.util.UUID;
public record HospitalResponse(UUID id, String name) { public static HospitalResponse from(Hospital hospital) { return new HospitalResponse(hospital.getId(), hospital.getName()); } }
