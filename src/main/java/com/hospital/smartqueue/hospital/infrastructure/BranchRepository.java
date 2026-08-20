package com.hospital.smartqueue.hospital.infrastructure;
import com.hospital.smartqueue.hospital.domain.Branch; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface BranchRepository extends JpaRepository<Branch,UUID>{ List<Branch> findByHospitalId(UUID hospitalId); }
