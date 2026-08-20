package com.hospital.smartqueue.patient.infrastructure;

import com.hospital.smartqueue.patient.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByPatientNumberIgnoreCase(String patientNumber);

    @Query("""
            select p from Patient p
            where lower(p.patientNumber) like lower(concat('%', :query, '%'))
               or lower(concat(p.firstName, ' ', p.lastName)) like lower(concat('%', :query, '%'))
               or p.mobileNumber like concat('%', :query, '%')
            order by p.lastName, p.firstName, p.id
            """)
    Page<Patient> search(String query, Pageable pageable);
}
