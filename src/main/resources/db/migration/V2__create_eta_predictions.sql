create table eta_prediction_versions (
    id uuid primary key,
    hospital_id uuid not null,
    branch_id uuid not null,
    doctor_id uuid not null,
    service_date date not null,
    timing_revision bigint not null,
    version_number bigint not null,
    current_delay_seconds bigint not null check (current_delay_seconds >= 0),
    calculated_at timestamp with time zone not null,
    constraint uq_eta_prediction_scope_version unique (doctor_id, branch_id, service_date, version_number)
);

create index ix_eta_prediction_scope on eta_prediction_versions (hospital_id, branch_id, doctor_id, service_date, version_number desc);

create table upcoming_appointment_etas (
    id uuid primary key,
    prediction_version_id uuid not null references eta_prediction_versions(id),
    appointment_id uuid not null,
    scheduled_start timestamp with time zone not null,
    predicted_start timestamp with time zone not null,
    predicted_completion timestamp with time zone not null,
    sequence_number integer not null,
    constraint uq_eta_version_appointment unique (prediction_version_id, appointment_id)
);
