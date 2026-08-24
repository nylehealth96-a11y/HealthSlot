package com.hospital.smartqueue.doctor.infrastructure;
import com.hospital.smartqueue.doctor.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    boolean existsByHospitalIdAndCanonicalDoctorCode(UUID hospitalId, String canonicalDoctorCode);
    boolean existsByCanonicalProfessionalRegistrationNumber(String canonicalProfessionalRegistrationNumber);
    List<Doctor> findAllByHospitalIdOrderByNameAsc(UUID hospitalId);
    Optional<Doctor> findByIdAndHospitalId(UUID id, UUID hospitalId);
    @Query("select distinct d from Doctor d join d.departments department where d.hospitalId = :hospitalId and department.id = :departmentId order by d.name")
    List<Doctor> findAllByHospitalIdAndDepartmentId(UUID hospitalId, UUID departmentId);
    @Query("select count(d) > 0 from Doctor d join d.departments department where d.id = :doctorId and d.hospitalId = :hospitalId and department.branchId = :branchId")
    boolean existsByIdAndHospitalIdAndBranchId(UUID doctorId, UUID hospitalId, UUID branchId);
}
