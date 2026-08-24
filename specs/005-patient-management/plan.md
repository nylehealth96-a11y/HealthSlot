# Implementation Plan: Patient Registration and Profile Management

**Branch**: `002-patient-management` | **Date**: 2026-08-24 | **Spec**: [spec.md](spec.md)

## Summary

Add a hospital-scoped `patient` module to the Java 17 Spring Boot modular monolith. It will register, retrieve, search, and update patient profiles using UUID internal identifiers, globally unique human-readable patient numbers, PostgreSQL/Flyway migrations, validated versioned REST contracts, audit records, and optimistic concurrency control. Patient records are shared across a hospital's branches, but never exposed outside their hospital organization.

## Technical Context

**Language/Version**: Java 17
**Primary Dependencies**: Spring Boot 3.3.5; Spring Web; Spring Data JPA; Jakarta Validation; Flyway; PostgreSQL; JUnit 5; Testcontainers
**Storage**: PostgreSQL, authoritative schema managed with Flyway migrations; UTC `timestamptz` timestamps
**Testing**: JUnit 5 and AssertJ unit tests; Spring Boot, MockMvc, and PostgreSQL Testcontainers integration tests
**Target Platform**: Server-side REST application
**Project Type**: Backend modular monolith
**Performance Goals**: At least 95% of searches over 1,000 representative records complete within 2 seconds; registration completes within 3 minutes for at least 95% of observed staff attempts.
**Constraints**: `/api/v1` versioned APIs; feature-owned vertical packages; UUID internal identifiers; mobile number is non-unique and never a primary key; global error format; PII-safe logs and audit metadata; no appointment or scheduling changes.
**Scale/Scope**: One patient record is shared by all branches of its hospital organization. Search is restricted to that hospital and supports patient number, name, and mobile-number matching with a bounded, paginated result set.

## Constitution Check

### Pre-design gate

- **Domain boundaries — PASS**: Add only the feature-owned `patient` API, application, domain, and infrastructure packages; shared audit/error services are consumed through their intentional contracts.
- **PostgreSQL, UUIDs, and time — PASS**: Use a forward Flyway migration, UUID identifiers, and timestamp-with-time-zone columns.
- **Controlled state and concurrency — PASS**: Patient identity is immutable and stale profile updates are rejected through persisted optimistic versioning.
- **Versioned, secure, auditable APIs — CONDITIONAL PASS**: Endpoints are versioned, validated, scoped by hospital, and create PII-minimized audit events. The current codebase has no authentication or authorization mechanism; implementation must integrate a trusted staff context that evaluates staff role, branch affiliation, requested hospital, and patient ownership before endpoints are enabled. It must not accept actor identity or authorization solely from request input.
- **Testability — PASS**: Unit, integration, migration, validation, tenancy-isolation, and concurrent-update tests are planned.

No constitution exception is requested. The authorization-context integration is a delivery prerequisite, not an exception.

**Authorization dependency owner and readiness**: The HealthSlot platform security owner must provide the trusted staff context. The patient feature is ready to enable only when integration tests prove authenticated reception staff can access their authorized branches' hospital records and cannot access another hospital's records.

**Migration recovery**: Patient schema migrations are forward-only. A failed migration stops deployment; recovery is a reviewed forward corrective migration, not an automatic rollback.

## Project Structure

### Documentation (this feature)

```text
specs/005-patient-management/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── patient-api.md
└── tasks.md                 # Created by $speckit-tasks
```

### Source Code (repository root)

```text
src/main/java/com/hospital/smartqueue/
├── patient/{api,application,domain,infrastructure}/
└── common/{api,infrastructure}/
src/main/resources/db/migration/V2__create_patients.sql
src/test/java/com/hospital/smartqueue/patient/{application,infrastructure}/
src/test/java/com/hospital/smartqueue/integration/
```

**Structure Decision**: Follow the established feature-by-domain modular-monolith structure. The patient module owns its API, application service, domain objects, and repository; it references hospital identity and common auditing without accessing hospital persistence internals.

## Complexity Tracking

No added complexity requiring a constitution exception.

### Post-design gate

**PASS, subject to implementation prerequisite**: The design keeps the patient feature in one module, uses PostgreSQL/Flyway and UUIDs, keeps APIs under `/api/v1`, scopes records to hospitals, supplies audit events, and defines a testable optimistic-concurrency rule. Before production enablement, authenticated staff identity and authorization based on role, branch affiliation, requested hospital, and patient ownership must be available to the module; no insecure request-supplied actor shortcut is permitted.
