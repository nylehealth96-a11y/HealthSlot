CREATE TABLE patients (
    id UUID PRIMARY KEY,
    patient_number VARCHAR(40) NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    date_of_birth DATE NOT NULL,
    gender VARCHAR(20) NOT NULL CHECK (gender IN ('MALE', 'FEMALE', 'OTHER', 'PREFER_NOT_TO_SAY')),
    mobile_number VARCHAR(20) NOT NULL,
    email VARCHAR(254),
    address VARCHAR(1000),
    emergency_contact VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_patients_normalized_name
    ON patients (lower(btrim(first_name)), lower(btrim(last_name)));
CREATE INDEX idx_patients_mobile_number ON patients (mobile_number);
