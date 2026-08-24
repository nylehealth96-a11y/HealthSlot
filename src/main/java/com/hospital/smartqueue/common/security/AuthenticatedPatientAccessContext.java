package com.hospital.smartqueue.common.security;
import com.hospital.smartqueue.common.domain.DomainException; import org.springframework.stereotype.Service; import java.util.UUID;
/** Fails closed until a production identity provider is integrated. */
@Service public class AuthenticatedPatientAccessContext implements PatientAccessContext {
 public String staffId(){ throw new DomainException("UNAUTHENTICATED", "Authentication required"); }
 public void authorize(UUID hospitalId, UUID patientHospitalId){ throw new DomainException("UNAUTHENTICATED", "Authentication required"); }
}
