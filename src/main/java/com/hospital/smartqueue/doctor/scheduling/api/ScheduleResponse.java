package com.hospital.smartqueue.doctor.scheduling.api;
import com.hospital.smartqueue.doctor.scheduling.domain.*; import java.time.*; import java.util.*;
public record ScheduleResponse(UUID id,LocalDate effectiveStartDate,int slotDurationMinutes,long version){ public static ScheduleResponse from(DoctorScheduleRevision s){return new ScheduleResponse(s.getId(),s.getEffectiveFrom(),s.getSlotDurationMinutes(),s.getVersion());}}
