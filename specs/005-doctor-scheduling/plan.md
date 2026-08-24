# Implementation Plan: Doctor Scheduling

**Branch**: `003-doctor-scheduling` | **Date**: 2026-08-24 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/005-doctor-scheduling/spec.md`

## Summary

Provide effective-dated, branch-scoped recurring doctor schedules, breaks, leave, date exceptions, and deterministic availability projection. Implement a `doctor.scheduling` feature module using Spring MVC/JPA, PostgreSQL/Flyway, Java time, and a fail-closed scheduling identity port. Persist schedule rules and derive slots at read time; never persist or reserve slots in this feature.

## Technical Context


**Language/Version**: Java 17

**Primary Dependencies**: Spring Boot 3.3.5; Spring MVC, Spring Data JPA, Bean Validation, Flyway

**Storage**: PostgreSQL, with Flyway migrations; JPA validation-only schema management

**Testing**: JUnit 5, Spring Boot Test, MockMvc, Testcontainers PostgreSQL, Mockito

**Target Platform**: Server-side REST API

**Project Type**: Modular-monolith web service

**Performance Goals**: 31-day availability query completes within 2 seconds at p95

**Constraints**: `/api/v1` contract; UUID internal IDs; IANA branch timezone; no cross-midnight periods; no booking/reservation; production endpoints fail closed until real staff authentication is integrated; actor/scope never supplied in normal request input

**Scale/Scope**: One hospital-scoped, branch-scoped doctor schedule at a time; 31-day availability horizon used for acceptance validation

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Status | Evidence |
|---|---|---|
| Java modular-monolith feature boundary | PASS | New `doctor.scheduling` package owns API, application, domain, and infrastructure. |
| PostgreSQL, UUIDs, Flyway, explicit timezone | PASS | V2 migration creates scheduling schema and adds branch IANA timezone; all persisted instants remain UTC. |
| Safe scheduling state | PASS | Availability is derived deterministically; version conflicts reject stale writes. Booking is explicitly excluded. |
| Versioned, validated, auditable API | PASS | Contracts stay below `/api/v1`; Bean Validation + domain validation; audit events use trusted actor identity and PII-minimal metadata. |
| Testable delivery | PASS | Unit, integration, migration, precedence, and stale-update tests are planned. |

**Post-design check**: PASS. No constitution exception is required.

## Project Structure

### Documentation (this feature)

```text
specs/005-doctor-scheduling/
├── plan.md              # This file ($speckit-plan command output)
├── research.md          # Phase 0 output ($speckit-plan command)
├── data-model.md        # Phase 1 output ($speckit-plan command)
├── quickstart.md        # Phase 1 output ($speckit-plan command)
├── contracts/           # Phase 1 output ($speckit-plan command)
└── tasks.md             # Phase 2 output ($speckit-tasks command - NOT created by $speckit-plan)
```

### Source Code (repository root)

```text
src/main/java/com/hospital/smartqueue/
├── doctor/
│   ├── api/                         # existing doctor endpoints
│   └── scheduling/
│       ├── api/                     # versioned schedule and availability endpoints
│       ├── application/             # validation, authorization, projection service
│       ├── domain/                  # schedules, periods, breaks, leave, exceptions
│       └── infrastructure/          # JPA repositories and access-context adapters
└── common/
    ├── api/                         # global API errors
    └── infrastructure/              # audit service and clock

src/main/resources/db/migration/
└── V2__create_doctor_scheduling.sql

src/test/java/com/hospital/smartqueue/
├── doctor/scheduling/application/   # rule and slot-projection unit tests
├── doctor/scheduling/integration/   # PostgreSQL/MockMvc contract and stale-write tests
└── support/                         # TrustedTestSchedulingAccessContext (test sources only)
```

**Structure Decision**: Extend the existing Java modular monolith with `doctor.scheduling`, rather than a global scheduling package, because schedule ownership and authorization are anchored to doctors and branches. Cross-feature persistence access remains through intentional application/repository contracts.

## Complexity Tracking

No constitution violations or exceptions.
