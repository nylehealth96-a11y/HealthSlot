package com.hospital.smartqueue.doctor.api;
import com.hospital.smartqueue.doctor.domain.Doctor;
import com.hospital.smartqueue.doctor.domain.DoctorStatus;
import java.util.Set;
import java.util.UUID;
public record DoctorResponse(UUID id, UUID hospitalId, String doctorCode, String name, String specialization, String professionalRegistrationNumber, DoctorStatus status, Set<UUID> departmentIds) {
    public static DoctorResponse from(Doctor doctor) { return new DoctorResponse(doctor.getId(), doctor.getHospitalId(), doctor.getDoctorCode(), doctor.getName(), doctor.getSpecialization(), doctor.getProfessionalRegistrationNumber(), doctor.getStatus(), doctor.getDepartments().stream().map(department -> department.getId()).collect(java.util.stream.Collectors.toUnmodifiableSet())); }
}
