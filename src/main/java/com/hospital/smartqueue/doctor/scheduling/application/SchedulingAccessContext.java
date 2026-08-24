package com.hospital.smartqueue.doctor.scheduling.application;
import java.util.UUID;
public interface SchedulingAccessContext { StaffSchedulingIdentity current(); void requireRead(UUID hospitalId, UUID branchId, UUID doctorId); void requireManage(UUID hospitalId, UUID branchId, UUID doctorId); }
