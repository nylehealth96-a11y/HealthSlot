package com.hospital.smartqueue.hospital.api;
import com.hospital.smartqueue.hospital.domain.Branch;
import java.util.UUID;
public record BranchResponse(UUID id, UUID hospitalId, String name) { public static BranchResponse from(Branch branch) { return new BranchResponse(branch.getId(), branch.getHospitalId(), branch.getName()); } }
