package com.hospital.smartqueue.department.infrastructure;

import com.hospital.smartqueue.department.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    List<Department> findAllByBranchIdOrderByNameAsc(UUID branchId);
    boolean existsByBranchIdAndCanonicalName(UUID branchId, String canonicalName);
    @Query("select d from Department d join Branch b on d.branchId = b.id where d.id = :departmentId and b.hospitalId = :hospitalId")
    Optional<Department> findByIdAndHospitalId(UUID departmentId, UUID hospitalId);
}
