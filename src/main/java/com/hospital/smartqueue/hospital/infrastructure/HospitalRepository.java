package com.hospital.smartqueue.hospital.infrastructure;
import com.hospital.smartqueue.hospital.domain.Hospital; import org.springframework.data.jpa.repository.JpaRepository; import java.util.UUID;
public interface HospitalRepository extends JpaRepository<Hospital,UUID>{}
