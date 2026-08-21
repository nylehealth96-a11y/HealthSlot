CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE hospitals (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    canonical_name VARCHAR(200) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT hospitals_name_not_blank CHECK (length(btrim(name)) > 0)
);

CREATE TABLE branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hospital_id UUID NOT NULL REFERENCES hospitals(id),
    name VARCHAR(200) NOT NULL,
    canonical_name VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT branches_name_not_blank CHECK (length(btrim(name)) > 0),
    CONSTRAINT branches_hospital_canonical_name_unique UNIQUE (hospital_id, canonical_name)
);
CREATE INDEX branches_hospital_id_idx ON branches(hospital_id);

CREATE TABLE departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    branch_id UUID NOT NULL REFERENCES branches(id),
    name VARCHAR(200) NOT NULL,
    canonical_name VARCHAR(200) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT departments_name_not_blank CHECK (length(btrim(name)) > 0),
    CONSTRAINT departments_branch_canonical_name_unique UNIQUE (branch_id, canonical_name)
);
CREATE INDEX departments_branch_id_idx ON departments(branch_id);

CREATE TABLE doctors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hospital_id UUID NOT NULL REFERENCES hospitals(id),
    doctor_code VARCHAR(80) NOT NULL,
    canonical_doctor_code VARCHAR(80) NOT NULL,
    name VARCHAR(200) NOT NULL,
    specialization VARCHAR(200) NOT NULL,
    professional_registration_number VARCHAR(120) NOT NULL,
    canonical_professional_registration_number VARCHAR(120) NOT NULL UNIQUE,
    status VARCHAR(16) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT doctors_code_not_blank CHECK (length(btrim(doctor_code)) > 0),
    CONSTRAINT doctors_name_not_blank CHECK (length(btrim(name)) > 0),
    CONSTRAINT doctors_specialization_not_blank CHECK (length(btrim(specialization)) > 0),
    CONSTRAINT doctors_registration_not_blank CHECK (length(btrim(professional_registration_number)) > 0),
    CONSTRAINT doctors_status_check CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT doctors_hospital_canonical_code_unique UNIQUE (hospital_id, canonical_doctor_code)
);
CREATE INDEX doctors_hospital_id_idx ON doctors(hospital_id);

CREATE TABLE doctor_departments (
    doctor_id UUID NOT NULL REFERENCES doctors(id),
    department_id UUID NOT NULL REFERENCES departments(id),
    PRIMARY KEY (doctor_id, department_id)
);
CREATE INDEX doctor_departments_department_id_idx ON doctor_departments(department_id);

CREATE TABLE audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    occurred_at TIMESTAMPTZ NOT NULL,
    action VARCHAR(100) NOT NULL,
    target_type VARCHAR(100) NOT NULL,
    target_id UUID NOT NULL,
    hospital_id UUID REFERENCES hospitals(id),
    actor_reference VARCHAR(200),
    metadata VARCHAR(1000) NOT NULL DEFAULT '{}',
    CONSTRAINT audit_events_action_not_blank CHECK (length(btrim(action)) > 0),
    CONSTRAINT audit_events_target_type_not_blank CHECK (length(btrim(target_type)) > 0)
);
CREATE INDEX audit_events_target_idx ON audit_events(target_type, target_id);
CREATE INDEX audit_events_hospital_occurred_idx ON audit_events(hospital_id, occurred_at DESC);
