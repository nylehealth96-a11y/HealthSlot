# Implementation Plan: Appointment Check-In

**Branch**: `005-check-in` | **Date**: 2026-08-24 | **Spec**: [spec.md](./spec.md)

## Summary

Add a `checkin` module for reception staff to check in eligible booked appointments, create eligible walk-in queue entries, and retrieve in-scope queue entries. It uses UUID-backed PostgreSQL queue records, immutable human-readable references, database-backed duplicate prevention, branch-local time rules, PII-minimized audits, and server-side trusted staff identity. Production access fails closed until real authentication exists.

## Technical Context

**Language/Version**: Java 17  
**Primary Dependencies**: Spring Boot 3.3.5; Spring Web, Data JPA, Validation, Flyway; PostgreSQL JDBC; JUnit 5, Spring Boot Test, Testcontainers  
**Storage**: PostgreSQL via Flyway, UUID identifiers, `TIMESTAMPTZ` instants. The current branch contains only `V1__create_hospital_structure.sql`, so the next valid current-branch migration is `V2__create_queue_entries.sql`; prerequisite integration must reconcile any migration introduced by upstream feature merges before implementation begins.  
**Testing**: JUnit unit tests; Spring API/integration tests; PostgreSQL Testcontainers concurrency/persistence tests  
**Target Platform**: JVM Spring Boot REST service  
**Project Type**: Modular-monolith web service  
**Performance Goals**: SC-001 local warmed-Testcontainers creates (p95 <2 seconds; max <5 seconds) and SC-003 local warmed-Testcontainers scoped reads (p95 <2 seconds; max <5 seconds), using the exact workloads in the specification.  
**Constraints**: `/api/v1` routes; production identity fails closed; never take actor/role/hospital/branch/scope from normal request input; explicit branch timezone; no cross-module persistence access; transactions and database constraints protect races.  
**Scale/Scope**: `WAITING` queue entries only; appointment check-in, walk-in creation, and retrieval. Triage, calling, consultation, no-show, billing, and booking are out of scope.

## Constitution Check

### Pre-design gate: PASS

| Principle | Plan response |
|---|---|
| Modular monolith | `checkin` owns API, application, domain, and infrastructure; controllers are thin. |
| PostgreSQL, identity, time | Flyway, UUIDs, instants, and persisted branch timezone are explicit. |
| Concurrency and state | Transactions plus database uniqueness enforce idempotency and one active visit; initial state is `WAITING`. |
| Secure, audited APIs | Versioned validated API, trusted scope, non-disclosure, and PII-minimized audit events. |
| Testable delivery | Unit, API, persistence, and concurrency coverage are required. |

No exception or new infrastructure is required.

## Project Structure

```text
specs/005-check-in/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
└── contracts/check-in-api.md

src/main/java/com/hospital/smartqueue/
├── checkin/{api,application,domain,infrastructure}
├── common/
├── hospital/
├── patient/                 # upstream capability
├── appointment/             # upstream capability
└── doctor/scheduling/       # upstream capability

src/main/resources/db/migration/
└── V2__create_queue_entries.sql

src/test/java/com/hospital/smartqueue/
├── checkin/{application,api,infrastructure}/
└── support/
```

**Structure Decision**: `checkin` is independent. It calls application ports for patient, appointment, scheduling availability, branch timezone, audit, and trusted staff access, and never imports another module's repositories.

## Integration Boundaries

- Patient: resolve an existing scoped patient without disclosing out-of-scope records.
- Appointment: no appointment application module exists on `005-check-in` or the inspected upstream branches. The design prerequisite is an appointment module based on `specs/005-appointment-booking`: it must expose scoped appointment lookup and one transactional, appointment-module-owned `BOOKED`-without-link to `BOOKED`-with-queue-link operation. Its only currently specified statuses are `BOOKED` and `CANCELLED`; check-in must not add a status.
- Patient: the inspected `002-patient-management` branch provides `patient.application.PatientService` and `patient.infrastructure.PatientRepository`, but no documented scoped lookup contract. A public patient capability must be agreed/merged before check-in adapters can execute; check-in must not query that repository directly.
- Scheduling: the inspected `003-doctor-scheduling` branch provides `doctor.scheduling.application.DoctorAvailabilityService#available(hospitalId, branchId, doctorId, from, to)` and its scheduling repositories. It has no documented atomic reservation/availability contract for a walk-in. A scheduling-owned authoritative revalidation operation is a prerequisite; check-in must not access those repositories directly.
- Branch: provide membership and persisted IANA timezone; all local-date conversion follows IANA daylight-saving rules, never server defaults.
- Access: server-side staff ID, reception permission, and scope. `TestOnlyTrustedStaffContext` is test/local-only; production denies by default.
- Audit: store opaque IDs, source, scope, staff ID, and instants only--no names, contacts, or clinical data.

## Design Artifacts

- [Research decisions](./research.md)
- [Data model](./data-model.md)
- [API contract](./contracts/check-in-api.md)
- [Validation guide](./quickstart.md)

## Constitution Check (Post-design)

### Post-design gate: PASS

The design gives PostgreSQL authority over queue-reference, appointment-link, and active-visit invariants; applies branch-local time; keeps modules isolated through ports; validates boundary input; and makes tests use a test-only identity abstraction while production fails closed.

## Complexity Tracking

No violations requiring justification.
