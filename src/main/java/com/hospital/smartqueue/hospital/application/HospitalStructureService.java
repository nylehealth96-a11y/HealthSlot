package com.hospital.smartqueue.hospital.application;

import com.hospital.smartqueue.common.domain.CanonicalText;
import com.hospital.smartqueue.common.domain.ConflictException;
import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.common.infrastructure.AuditService;
import com.hospital.smartqueue.hospital.domain.Branch;
import com.hospital.smartqueue.hospital.domain.Hospital;
import com.hospital.smartqueue.hospital.infrastructure.BranchRepository;
import com.hospital.smartqueue.hospital.infrastructure.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class HospitalStructureService {
    private final HospitalRepository hospitals;
    private final BranchRepository branches;
    private final AuditService audit;
    public HospitalStructureService(HospitalRepository hospitals, BranchRepository branches, AuditService audit) { this.hospitals = hospitals; this.branches = branches; this.audit = audit; }
    @Transactional
    public Hospital createHospital(String name) {
        String display = CanonicalText.displayValue(name); String canonical = CanonicalText.normalize(name);
        if (hospitals.findByCanonicalName(canonical).isPresent()) throw new ConflictException("Hospital already exists");
        Hospital hospital = hospitals.save(new Hospital(display, canonical));
        audit.record("HOSPITAL_CREATED", "HOSPITAL", hospital.getId(), hospital.getId(), "{}");
        return hospital;
    }
    @Transactional(readOnly = true) public List<Hospital> listHospitals() { return hospitals.findAll(); }
    @Transactional
    public Branch createBranch(UUID hospitalId, String name) {
        requireHospital(hospitalId); String display = CanonicalText.displayValue(name); String canonical = CanonicalText.normalize(name);
        if (branches.existsByHospitalIdAndCanonicalName(hospitalId, canonical)) throw new ConflictException("Branch already exists");
        Branch branch = branches.save(new Branch(hospitalId, display, canonical));
        audit.record("BRANCH_CREATED", "BRANCH", branch.getId(), hospitalId, "{}");
        return branch;
    }
    @Transactional(readOnly = true) public List<Branch> listBranches(UUID hospitalId) { requireHospital(hospitalId); return branches.findAllByHospitalIdOrderByNameAsc(hospitalId); }
    private Hospital requireHospital(UUID hospitalId) { return hospitals.findById(hospitalId).orElseThrow(() -> new NotFoundException("Requested resource was not found")); }
}
