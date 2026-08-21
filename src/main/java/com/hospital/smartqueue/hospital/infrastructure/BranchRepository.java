package com.hospital.smartqueue.hospital.infrastructure;

import com.hospital.smartqueue.hospital.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {
    List<Branch> findAllByHospitalIdOrderByNameAsc(UUID hospitalId);
    Optional<Branch> findByIdAndHospitalId(UUID id, UUID hospitalId);
    boolean existsByHospitalIdAndCanonicalName(UUID hospitalId, String canonicalName);
}
