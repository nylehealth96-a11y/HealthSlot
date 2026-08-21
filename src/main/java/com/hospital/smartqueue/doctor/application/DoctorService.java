package com.hospital.smartqueue.doctor.application;
import com.hospital.smartqueue.common.domain.*;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.department.domain.Department;
import com.hospital.smartqueue.department.infrastructure.DepartmentRepository;
import com.hospital.smartqueue.doctor.domain.*;
import com.hospital.smartqueue.doctor.infrastructure.DoctorRepository;
import com.hospital.smartqueue.hospital.infrastructure.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class DoctorService {
    private final HospitalRepository hospitals; private final DepartmentRepository departments; private final DoctorRepository doctors; private final AuditService audit;
    public DoctorService(HospitalRepository hospitals, DepartmentRepository departments, DoctorRepository doctors, AuditService audit) { this.hospitals = hospitals; this.departments = departments; this.doctors = doctors; this.audit = audit; }
    @Transactional
    public Doctor register(UUID hospitalId, String code, String name, String specialization, String registrationNumber, DoctorStatus status, Set<UUID> departmentIds) {
        requireHospital(hospitalId); String canonicalCode = CanonicalText.normalize(code); String canonicalRegistration = CanonicalText.normalize(registrationNumber);
        if (doctors.existsByHospitalIdAndCanonicalDoctorCode(hospitalId, canonicalCode) || doctors.existsByCanonicalProfessionalRegistrationNumber(canonicalRegistration)) throw new ConflictException("Doctor already exists");
        Set<Department> selectedDepartments = new LinkedHashSet<>();
        for (UUID departmentId : departmentIds) selectedDepartments.add(departments.findByIdAndHospitalId(departmentId, hospitalId).orElseThrow(() -> new NotFoundException("Requested resource was not found")));
        if (selectedDepartments.isEmpty()) throw new DomainException("VALIDATION_ERROR", "At least one department is required");
        Doctor doctor = doctors.save(new Doctor(hospitalId, CanonicalText.displayValue(code), canonicalCode, CanonicalText.displayValue(name), CanonicalText.displayValue(specialization), CanonicalText.displayValue(registrationNumber), canonicalRegistration, status == null ? DoctorStatus.ACTIVE : status, selectedDepartments));
        audit.record("DOCTOR_REGISTERED", "DOCTOR", doctor.getId(), hospitalId, "{}"); return doctor;
    }
    @Transactional(readOnly = true) public List<Doctor> list(UUID hospitalId) { requireHospital(hospitalId); return doctors.findAllByHospitalIdOrderByNameAsc(hospitalId); }
    @Transactional(readOnly = true) public List<Doctor> listByDepartment(UUID hospitalId, UUID departmentId) { departments.findByIdAndHospitalId(departmentId, hospitalId).orElseThrow(() -> new NotFoundException("Requested resource was not found")); return doctors.findAllByHospitalIdAndDepartmentId(hospitalId, departmentId); }
    @Transactional public Doctor setStatus(UUID hospitalId, UUID doctorId, DoctorStatus status) { Doctor doctor = doctors.findByIdAndHospitalId(doctorId, hospitalId).orElseThrow(() -> new NotFoundException("Requested resource was not found")); if (doctor.changeStatus(status)) audit.record("DOCTOR_STATUS_CHANGED", "DOCTOR", doctor.getId(), hospitalId, "{}"); return doctor; }
    private void requireHospital(UUID hospitalId) { if (!hospitals.existsById(hospitalId)) throw new NotFoundException("Requested resource was not found"); }
}
