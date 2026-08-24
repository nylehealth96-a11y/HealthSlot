package com.hospital.smartqueue.doctor.scheduling.application;

import com.hospital.smartqueue.common.domain.DomainException;
import com.hospital.smartqueue.common.domain.NotFoundException;
import com.hospital.smartqueue.doctor.scheduling.domain.WorkingPeriod;
import com.hospital.smartqueue.doctor.scheduling.infrastructure.DoctorLeaveRepository;
import com.hospital.smartqueue.doctor.scheduling.infrastructure.DoctorScheduleRevisionRepository;
import com.hospital.smartqueue.doctor.scheduling.infrastructure.ScheduleExceptionRepository;
import com.hospital.smartqueue.hospital.infrastructure.BranchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DoctorAvailabilityService {
    private final SchedulingAccessContext access;
    private final BranchRepository branches;
    private final DoctorScheduleRevisionRepository schedules;
    private final DoctorLeaveRepository leaves;
    private final ScheduleExceptionRepository exceptions;

    public DoctorAvailabilityService(SchedulingAccessContext access, BranchRepository branches, DoctorScheduleRevisionRepository schedules, DoctorLeaveRepository leaves, ScheduleExceptionRepository exceptions) {
        this.access = access; this.branches = branches; this.schedules = schedules; this.leaves = leaves; this.exceptions = exceptions;
    }

    @Transactional(readOnly = true)
    public List<AvailableSlot> available(UUID hospitalId, UUID branchId, UUID doctorId, LocalDate from, LocalDate to) {
        access.requireRead(hospitalId, branchId, doctorId);
        if (from == null || to == null || to.isBefore(from) || from.plusDays(30).isBefore(to)) throw new DomainException("VALIDATION_ERROR", "Date range must be between one and 31 days");
        ZoneId zone = ZoneId.of(branches.findByIdAndHospitalId(branchId, hospitalId).orElseThrow(() -> new NotFoundException("Requested resource was not found")).getTimezone());
        var result = new ArrayList<AvailableSlot>();
        for (var date = from; !date.isAfter(to); date = date.plusDays(1)) {
            if (!leaves.findOverlapping(doctorId, branchId, date, date).isEmpty() || exceptions.existsByDoctorIdAndBranchIdAndExceptionDate(doctorId, branchId, date)) continue;
            var revision = schedules.findFirstByDoctorIdAndBranchIdAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(doctorId, branchId, date).orElse(null);
            if (revision == null) continue;
            for (WorkingPeriod period : revision.getPeriods()) {
                if (period.day() != date.getDayOfWeek().getValue()) continue;
                for (LocalTime start = period.start(); !start.plusMinutes(revision.getSlotDurationMinutes()).isAfter(period.end()); start = start.plusMinutes(revision.getSlotDurationMinutes())) {
                    LocalTime slotStart = start;
                    LocalTime end = start.plusMinutes(revision.getSlotDurationMinutes());
                    boolean breakOverlap = period.breaks().stream().anyMatch(b -> slotStart.isBefore(b.end()) && b.start().isBefore(end));
                    if (!breakOverlap) result.add(new AvailableSlot(doctorId, branchId, ZonedDateTime.of(date, start, zone).toInstant(), ZonedDateTime.of(date, end, zone).toInstant()));
                }
            }
        }
        return List.copyOf(result);
    }
}
