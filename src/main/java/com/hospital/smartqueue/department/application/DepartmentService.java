package com.hospital.smartqueue.department.application;

import com.hospital.smartqueue.common.domain.CanonicalText;
import com.hospital.smartqueue.common.domain.ConflictException;
import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.department.domain.Department;
import com.hospital.smartqueue.department.infrastructure.DepartmentRepository;
import com.hospital.smartqueue.hospital.domain.Branch;
import com.hospital.smartqueue.hospital.infrastructure.BranchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class DepartmentService {
    private final BranchRepository branches; private final DepartmentRepository departments; private final AuditService audit;
    public DepartmentService(BranchRepository branches, DepartmentRepository departments, AuditService audit) { this.branches = branches; this.departments = departments; this.audit = audit; }
    @Transactional
    public Department create(UUID hospitalId, UUID branchId, String name) {
        Branch branch = requireBranch(hospitalId, branchId); String display = CanonicalText.displayValue(name); String canonical = CanonicalText.normalize(name);
        if (departments.existsByBranchIdAndCanonicalName(branchId, canonical)) throw new ConflictException("Department already exists");
        Department department = departments.save(new Department(branchId, display, canonical));
        audit.record("DEPARTMENT_CREATED", "DEPARTMENT", department.getId(), branch.getHospitalId(), "{}");
        return department;
    }
    @Transactional(readOnly = true) public List<Department> list(UUID hospitalId, UUID branchId) { requireBranch(hospitalId, branchId); return departments.findAllByBranchIdOrderByNameAsc(branchId); }
    private Branch requireBranch(UUID hospitalId, UUID branchId) { return branches.findByIdAndHospitalId(branchId, hospitalId).orElseThrow(() -> new NotFoundException("Requested resource was not found")); }
}
