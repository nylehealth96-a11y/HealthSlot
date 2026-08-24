package com.hospital.smartqueue.doctor.scheduling.api;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import java.time.*; import java.util.*;
public record ScheduleRequest(@NotNull LocalDate effectiveStartDate,@Positive int slotDurationMinutes,@NotEmpty List<@Valid Day> days){ public record Day(@NotNull DayOfWeek dayOfWeek,@NotEmpty List<@Valid Period> workingPeriods){} public record Period(@NotNull LocalTime startTime,@NotNull LocalTime endTime,List<@Valid Break> breaks){} public record Break(@NotNull LocalTime startTime,@NotNull LocalTime endTime){} }
