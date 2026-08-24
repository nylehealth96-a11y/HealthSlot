CREATE TABLE patients (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), hospital_id UUID NOT NULL REFERENCES hospitals(id), patient_number VARCHAR(80) NOT NULL UNIQUE,
 first_name VARCHAR(120) NOT NULL, last_name VARCHAR(120) NOT NULL, canonical_first_name VARCHAR(120) NOT NULL, canonical_last_name VARCHAR(120) NOT NULL,
 date_of_birth DATE NOT NULL, gender VARCHAR(24) NOT NULL, mobile_number VARCHAR(40) NOT NULL, normalized_mobile_number VARCHAR(40) NOT NULL,
 email VARCHAR(254), address VARCHAR(1000), emergency_contact_name VARCHAR(200), emergency_contact_relationship VARCHAR(100), emergency_contact_mobile_number VARCHAR(40),
 version BIGINT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 CONSTRAINT patients_emergency_contact_complete CHECK ((emergency_contact_name IS NULL AND emergency_contact_relationship IS NULL AND emergency_contact_mobile_number IS NULL) OR (emergency_contact_name IS NOT NULL AND emergency_contact_relationship IS NOT NULL AND emergency_contact_mobile_number IS NOT NULL))
);
CREATE INDEX patients_hospital_name_idx ON patients(hospital_id, canonical_last_name, canonical_first_name);
CREATE INDEX patients_hospital_mobile_idx ON patients(hospital_id, normalized_mobile_number);
