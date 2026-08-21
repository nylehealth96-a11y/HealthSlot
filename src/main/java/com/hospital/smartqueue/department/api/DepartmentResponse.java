package com.hospital.smartqueue.department.api;
import com.hospital.smartqueue.department.domain.Department;
import java.util.UUID;
public record DepartmentResponse(UUID id, UUID branchId, String name) { public static DepartmentResponse from(Department department) { return new DepartmentResponse(department.getId(), department.getBranchId(), department.getName()); } }
