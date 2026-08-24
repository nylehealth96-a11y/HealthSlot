# Research: Appointment Check-In

## Queue-entry representation

**Decision**: Use one `QueueEntry` aggregate with `APPOINTMENT_CHECK_IN` and `WALK_IN` sources and an optional appointment UUID.

**Rationale**: Both workflows need the same ordering, retrieval, audit, and duplicate protection. Source retains provenance; walk-ins deliberately lack an appointment.

**Alternatives considered**: Separate tables (splits queue invariants); reuse appointment as queue record (walk-ins have no appointment).

## Concurrent duplicate prevention and idempotency

**Decision**: Create entries transactionally and make PostgreSQL authoritative: unique non-null appointment link and partial unique active-visit index on `(patient_id, doctor_id, branch_id, visit_date)` where status is `WAITING`.

**Rationale**: Application checks race. A duplicate appointment request reloads its scoped existing entry; a duplicate walk-in returns conflict.

**Alternatives considered**: JVM locks (not multi-instance safe); isolation level only (schema invariant remains necessary).

## Queue reference

**Decision**: Generate immutable PII-free references such as `Q-YYYYMMDD-<secure-random-token>`, with a database uniqueness constraint and a retry only for reference collision.

**Rationale**: Reception-readable, globally unique, never reused, and no patient data leakage.

**Alternatives considered**: Mobile/patient numbers (PII/reuse); daily sequence alone (global/concurrent uniqueness complexity).

## Time and eligibility

**Decision**: Persist instants; derive `visitDate` from persisted branch IANA timezone; allow check-in from branch-local start of the appointment date through scheduled end inclusive.

**Rationale**: Meets the spec without server-timezone ambiguity and makes uniqueness indexable.

**Alternatives considered**: Server timezone (forbidden); only recompute date at read time (cannot enforce DB invariant).

## Appointment check-in lifecycle and atomicity

**Decision**: Reuse the appointment feature's stated statuses, `BOOKED` and `CANCELLED`. Check-in is the appointment-owned operational transition `BOOKED` without a linked queue entry to `BOOKED` with one linked queue entry; it does not introduce `CHECKED_IN`. `CANCELLED` and every non-`BOOKED` state conflict. The appointment link/state mutation, queue entry, and audit record commit in one transaction or all roll back.

**Rationale**: This honors the upstream lifecycle rather than creating a conflicting status, satisfies controlled transition rules, and avoids partial clinical-operational state.

**Alternatives considered**: A `CHECKED_IN` appointment status (conflicts with upstream specification); eventual audit/link repair (allows prohibited partial success).

## Upstream readiness and walk-in race

**Decision**: Treat upstream application contracts as integration prerequisites. Patient lookup is absent from the inspected patient module; appointment implementation is absent; scheduling exposes derived `available(...)` but no authoritative atomic walk-in revalidation. The required providers must supply these contracts before dependent check-in work starts. At walk-in commit, the scheduling provider revalidates availability; if it changed, no queue row or audit persists and a non-disclosing conflict is returned. A simultaneous appointment check-in/walk-in uses the active-visit unique constraint: first committing transaction wins, the loser conflicts.

**Rationale**: Check-in must not duplicate or directly access upstream persistence, and database authority gives a deterministic cross-source outcome.

## Authorization, availability, and audit

**Decision**: A `TrustedStaffAccess` port supplies trusted staff ID, reception capability, and scope. The production adapter denies all access; `TestOnlyTrustedStaffContext` is test-source/local-verification-only. A scheduling port confirms an available branch slot before walk-in persistence. Audits use only opaque IDs, event type, source, scope, staff ID, and instant.

**Rationale**: Request data cannot forge access; modules remain isolated; FR-003 and PII minimization remain enforceable.

**Alternatives considered**: Request actor fields (forgeable); direct scheduling repository access (breaks module boundaries); full request logging (unnecessary PII exposure).
