package com.hospital.smartqueue.doctor.scheduling.infrastructure;
import com.hospital.smartqueue.common.domain.DomainException; import com.hospital.smartqueue.doctor.scheduling.application.*; import org.springframework.stereotype.Service; import java.util.UUID;
@Service public class UnauthenticatedSchedulingAccessContext implements SchedulingAccessContext {
 private DomainException denied(){ return new DomainException("UNAUTHENTICATED", "Scheduling authentication is not configured"); }
 public StaffSchedulingIdentity current(){ throw denied(); } public void requireRead(UUID h, UUID b, UUID d){ throw denied(); } public void requireManage(UUID h, UUID b, UUID d){ throw denied(); }
}
