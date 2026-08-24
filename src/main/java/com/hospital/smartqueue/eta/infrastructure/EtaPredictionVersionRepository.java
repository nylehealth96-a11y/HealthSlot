package com.hospital.smartqueue.eta.infrastructure;

import com.hospital.smartqueue.eta.domain.EtaPredictionVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface EtaPredictionVersionRepository extends JpaRepository<EtaPredictionVersion, UUID> {
    Optional<EtaPredictionVersion> findTopByHospitalIdAndBranchIdAndDoctorIdAndServiceDateOrderByVersionNumberDesc(
            UUID hospitalId, UUID branchId, UUID doctorId, LocalDate serviceDate);
}
