package com.hospital.smartqueue.doctor.scheduling.application;
import java.util.Set; import java.util.UUID;
public record StaffSchedulingIdentity(UUID staffId, SchedulingRole role, Set<UUID> hospitalIds, Set<UUID> branchIds, Set<UUID> doctorIds) { }
