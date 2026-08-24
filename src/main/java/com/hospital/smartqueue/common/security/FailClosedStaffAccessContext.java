package com.hospital.smartqueue.common.security;

import com.hospital.smartqueue.common.domain.DomainException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FailClosedStaffAccessContext implements StaffAccessContext {
    @Override public StaffIdentity requireAppointmentScheduler(UUID hospitalId, UUID branchId) {
        throw new DomainException("UNAUTHENTICATED", "Authentication integration is required");
    }
}
