# Implementation Plan: Doctor Consultation Lifecycle

**Branch**: `007-doctor-consultation` | **Date**: 2026-08-24 | **Spec**: [spec.md](./spec.md)

## Summary

Add an auditable consultation lifecycle owned by a consultation module: an in-scope receptionist calls a check-in-created waiting consultation, the assigned doctor starts it, and the assigned doctor completes it. Each permitted transition records authoritative timing where applicable and triggers a reliable, scoped queue recalculation after completion.

## Technical Context

**Language/Version**: Java 17

**Primary Dependencies**: Spring Boot REST and validation, JPA, Flyway, PostgreSQL, JUnit, Testcontainers

**Storage**: PostgreSQL with UUID identifiers, `TIMESTAMPTZ` lifecycle timestamps, optimistic versioning, and audited state transitions

**Testing**: Unit lifecycle tests; API authorization/validation tests; PostgreSQL integration tests for transactional audit persistence and concurrent mutations; contract tests for non-disclosure

**Target Platform**: Existing HealthSlot REST service

**Project Type**: Modular-monolith REST service

**Performance Goals**: Lifecycle transition tests demonstrate a valid call, start, or complete operation within two seconds in the documented local integration-test fixture; this is a project validation target rather than a production-capacity claim.

**Constraints**: Versioned `/api/v1` endpoints; no caller-supplied identity or scope; production endpoints fail closed until real authentication exists; test-only trusted staff context is test/local-only; no cross-module persistence access; no partial state/timing/audit/queue result; branch timezone is explicit for display.

**Scale/Scope**: One consultation is tied to one patient, doctor, hospital, branch, and authoritative check-in source. Cancellation, no-shows, medical records, prescriptions, billing, notifications, and queue implementation are out of scope.

## Constitution Check

### Pre-design

| Principle | Result | Design response |
|---|---|---|
| I. Domain boundaries | PASS | A `consultation` module owns its API, application logic, domain model, and persistence. Dependencies are ports/contracts only. |
| II. PostgreSQL, identity, time | PASS | UUIDs, migration, transactional persistence, trusted context abstraction, instants, and branch timezone are required. |
| III. Controlled state | PASS | The four lifecycle states, allowed transitions, optimistic locking, and concurrency tests are explicit. |
| IV. Secure, auditable API | PASS | `/api/v1`, validation, fail-closed authorization, non-disclosure, and PII-minimized audit are planned. |
| V. Testable delivery | PASS | Unit, API, persistence, rollback, and concurrency tests are required. |

### Integration prerequisite

The current branch contains only hospital, doctor, department, and common infrastructure. It does **not** yet contain patient management, check-in queue entries, queue recalculation, branch timezone, or real trusted-identity modules. Planning can define their contracts, but implementation must first integrate the authoritative upstream modules or remain blocked; it must not recreate their data stores inside consultation.

### Post-design

PASS subject to the integration prerequisite above. The design uses intentional contracts, keeps production authentication fail-closed, and requires an atomic database transaction for each lifecycle mutation and required audit. Completion and queue recalculation use a transactional outbox/recovery boundary if the queue module is not co-transactional; the user-visible completion is not acknowledged until the contract's atomicity/recovery guarantee is met.

## Project Structure

```text
src/main/java/com/hospital/smartqueue/
└── consultation/
    ├── api/
    ├── application/
    ├── domain/
    └── infrastructure/

src/main/resources/db/migration/
└── V<next-contiguous>__create_consultations.sql

src/test/java/com/hospital/smartqueue/consultation/
├── api/
├── application/
└── infrastructure/

specs/007-doctor-consultation/
├── research.md
├── data-model.md
├── quickstart.md
└── contracts/consultation-api.md
```

**Structure Decision**: The consultation feature is a self-contained domain module. It owns consultation records and lifecycle transitions, but uses ports for check-in, trusted identity, branch timezone, queue recalculation, and audit recording. The migration version is selected only after the upstream migration baseline has been integrated and inspected.
