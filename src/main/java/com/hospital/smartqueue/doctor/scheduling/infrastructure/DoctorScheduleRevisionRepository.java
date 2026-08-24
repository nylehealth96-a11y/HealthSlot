package com.hospital.smartqueue.doctor.scheduling.infrastructure;
import com.hospital.smartqueue.doctor.scheduling.domain.DoctorScheduleRevision; import org.springframework.data.jpa.repository.*; import java.time.*; import java.util.*;
public interface DoctorScheduleRevisionRepository extends JpaRepository<DoctorScheduleRevision,UUID>{ Optional<DoctorScheduleRevision> findFirstByDoctorIdAndBranchIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(UUID doctorId,UUID branchId,LocalDate date); }
