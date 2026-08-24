# Research: Appointment Booking

## Availability ownership

**Decision**: The doctor-scheduling module supplies derived candidate slots through an application port; appointment booking consumes that port and never queries scheduling persistence directly.

**Rationale**: Recurring rules, leave, exceptions, breaks, branch timezone conversion, and slot duration stay owned by scheduling. This avoids a cyclic dependency while allowing appointments to remove occupied slots from the returned result.

**Alternatives considered**: Reimplement schedule calculation in appointment (rejected: duplicate rules); make scheduling query appointment tables (rejected: cyclic ownership and leakage).

## Double-booking protection

**Decision**: Persist appointments with a PostgreSQL partial exclusion constraint over doctor, branch, and `[start_at, end_at)` when status is `BOOKED`, supported by a transactional booking service. Use `btree_gist` and `tstzrange`.

**Rationale**: The constraint is the final authority under concurrent transactions and also blocks accidental overlaps, while allowing adjacent slots. Updating status to `CANCELLED` immediately releases the interval. A pre-check remains advisory only.

**Alternatives considered**: Application-only availability check (rejected: races); partial unique constraint over exact start/end (rejected: permits overlaps); SERIALIZABLE or explicit locks (rejected: the database invariant is simpler and sufficient).

## Stale changes and atomic reschedule

**Decision**: Add an optimistic version to `Appointment`; cancel and reschedule require the caller-provided current version. Reschedule updates the same aggregate in one transaction.

**Rationale**: Optimistic locking detects stale staff actions. A single update preserves the appointment number and rolls back fully when the target interval conflicts, leaving the original booking intact.

**Alternatives considered**: Last-write-wins (rejected: unsafe operational changes); cancel then create in separate transactions (rejected: can lose the original booking).

## Time and availability

**Decision**: Accept date/time inputs as offset-aware values, use the branch's modeled IANA zone for schedule/date-range calculations, and store resolved appointment instants as `TIMESTAMPTZ`.

**Rationale**: Persisted instants are unambiguous, while branch-local operational dates correctly handle daylight-saving changes and avoid server-default timezone behavior.

**Alternatives considered**: Server-local time (rejected: inconsistent); storing local timestamps without an offset (rejected: ambiguous around DST).

## Security and audit boundary

**Decision**: Application services depend on a `StaffAccessContext` port. Its runtime adapter fails closed until real authentication is available; a clearly named test-only implementation is created only in test sources/configuration.

**Rationale**: The business service receives trusted `staffId`, roles, and resolved hospital/branch scope without accepting identity claims in headers, query parameters, paths, or request bodies. Audit records use the trusted staff ID and PII-free metadata.

**Alternatives considered**: Client-supplied identity/scope (rejected: privilege escalation); temporary production default identity (rejected: could be mistaken for authentication).

## Appointment numbers

**Decision**: Generate a human-readable immutable appointment number and enforce a global database unique constraint; retry a generated collision in the application boundary if needed.

**Rationale**: The number is usable by staff while PostgreSQL remains the authority for uniqueness.

**Alternatives considered**: UUID as the displayed number (rejected: not operationally readable); application-only uniqueness check (rejected: concurrent collisions).
