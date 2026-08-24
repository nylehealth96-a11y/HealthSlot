package com.hospital.smartqueue.eta.infrastructure;

import com.hospital.smartqueue.eta.domain.UpcomingAppointmentEta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UpcomingAppointmentEtaRepository extends JpaRepository<UpcomingAppointmentEta, UUID> {
    List<UpcomingAppointmentEta> findByPredictionVersionIdOrderBySequenceNumber(UUID predictionVersionId);
}
