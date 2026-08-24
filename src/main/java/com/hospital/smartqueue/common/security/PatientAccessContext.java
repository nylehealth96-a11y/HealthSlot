package com.hospital.smartqueue.common.security;
import java.util.UUID;
public interface PatientAccessContext { String staffId(); void authorize(UUID hospitalId, UUID patientHospitalId); }
