create table notification_intents (
 id uuid primary key, source_event_id uuid not null, notification_type varchar(40) not null,
 hospital_id uuid not null, branch_id uuid not null, appointment_or_visit_id uuid not null,
 recipient_class varchar(20) not null, status varchar(20) not null, attempt_count integer not null default 0,
 created_at timestamp with time zone not null, updated_at timestamp with time zone not null,
 constraint uq_notification_source_type unique(source_event_id,notification_type)
);
create index ix_notification_scope on notification_intents(hospital_id,branch_id,created_at desc);
