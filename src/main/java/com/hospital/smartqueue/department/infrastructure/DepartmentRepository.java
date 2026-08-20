package com.hospital.smartqueue.department.infrastructure;
import com.hospital.smartqueue.department.domain.Department; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface DepartmentRepository extends JpaRepository<Department,UUID>{ List<Department> findByBranchId(UUID branchId); }
