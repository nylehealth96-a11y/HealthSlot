ALTER TABLE branches ADD COLUMN timezone VARCHAR(64) NOT NULL DEFAULT 'UTC';

CREATE TABLE doctor_schedule_revisions (
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(), doctor_id UUID NOT NULL REFERENCES doctors(id), branch_id UUID NOT NULL REFERENCES branches(id), effective_from DATE NOT NULL, slot_duration_minutes INTEGER NOT NULL CHECK (slot_duration_minutes > 0), version BIGINT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
 UNIQUE (doctor_id, branch_id, effective_from));
CREATE TABLE schedule_working_periods (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), schedule_revision_id UUID NOT NULL REFERENCES doctor_schedule_revisions(id) ON DELETE CASCADE, day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7), start_time TIME NOT NULL, end_time TIME NOT NULL, CHECK (start_time < end_time));
CREATE TABLE schedule_breaks (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), working_period_id UUID NOT NULL REFERENCES schedule_working_periods(id) ON DELETE CASCADE, start_time TIME NOT NULL, end_time TIME NOT NULL, CHECK (start_time < end_time));
CREATE TABLE doctor_leaves (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), doctor_id UUID NOT NULL REFERENCES doctors(id), branch_id UUID NOT NULL REFERENCES branches(id), start_date DATE NOT NULL, end_date DATE NOT NULL, version BIGINT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, CHECK (start_date <= end_date));
CREATE TABLE schedule_exceptions (id UUID PRIMARY KEY DEFAULT gen_random_uuid(), doctor_id UUID NOT NULL REFERENCES doctors(id), branch_id UUID NOT NULL REFERENCES branches(id), exception_date DATE NOT NULL, version BIGINT NOT NULL DEFAULT 0, created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, UNIQUE (doctor_id, branch_id, exception_date));
CREATE INDEX doctor_schedule_revisions_lookup_idx ON doctor_schedule_revisions(doctor_id, branch_id, effective_from DESC);
CREATE INDEX doctor_leaves_lookup_idx ON doctor_leaves(doctor_id, branch_id, start_date, end_date);
