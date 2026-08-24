package com.hospital.smartqueue.doctor.scheduling.infrastructure;
import com.hospital.smartqueue.doctor.scheduling.domain.ScheduleException; import org.springframework.data.jpa.repository.JpaRepository; import java.time.*; import java.util.*;
public interface ScheduleExceptionRepository extends JpaRepository<ScheduleException,UUID>{ boolean existsByDoctorIdAndBranchIdAndExceptionDate(UUID doctorId,UUID branchId,LocalDate date); Optional<ScheduleException> findByIdAndDoctorIdAndBranchId(UUID id,UUID doctorId,UUID branchId); }
