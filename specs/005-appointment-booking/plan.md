# Implementation Plan: Appointment Booking

**Branch**: `004-appointment-booking` | **Date**: 2026-08-24 | **Spec**: [spec.md](spec.md)

## Summary

Deliver a Java 17/Spring Boot appointment module that exposes scoped available-slot, booking, retrieval, cancellation, and rescheduling operations. Scheduling remains the authority for derived slots; the appointment module owns reservations, state transitions, immutable appointment numbers, and audit events. PostgreSQL constraints and transactions provide the final protection against double booking; a trusted staff-identity port keeps production endpoints fail-closed until real authentication is integrated.

## Technical Context

**Language/Version**: Java 17

**Primary Dependencies**: Spring Boot 3.3, Spring Web, Spring Data JPA, Jakarta Validation, Flyway, PostgreSQL, JUnit 5, Spring Boot Test, Testcontainers PostgreSQL

**Storage**: PostgreSQL, Flyway migrations, `UUID` identifiers, `TIMESTAMPTZ` appointment instants

**Testing**: JUnit 5 unit tests plus PostgreSQL Testcontainers integration tests, including simultaneous booking attempts

**Target Platform**: Server-side REST API

**Project Type**: Backend modular monolith

**Performance Goals**: 95% of 31-day availability/retrieval requests under 2 seconds; booking confirmation under 30 seconds from selection; concurrent same-slot requests create exactly one `BOOKED` appointment.

**Constraints**: `/api/v1` DTO-only controllers; branch timezone converts local schedule/date requests to persisted UTC instants; `APPOINTMENT_SCHEDULER` plus hospital/branch scope is required; no actor, role, staff, hospital, branch, or authorization scope accepted from normal request input; production operations fail closed without real authentication; PII-minimized operational signals are required; no new distributed infrastructure.

**Scale/Scope**: One hospital/branch scoped reservation per request, availability ranges no longer than 31 calendar days, statuses `BOOKED` and `CANCELLED`, and no reminders, payment, check-in, waitlist, queue, or recurrence features.

## Constitution Check

### Pre-design gate: PASS

- The module uses feature-owned `appointment/{api,application,domain,infrastructure}` packages; scheduling is consumed through an application port rather than its persistence layer.
- PostgreSQL/Flyway, UUIDs, explicit branch timezone conversion, versioned REST endpoints, DTO validation, global errors, PII-minimized audit records, and automated tests are required.
- A database exclusion constraint plus transactional application service is required; application availability checks alone are explicitly insufficient.
- Production authentication is not invented. A fail-closed production adapter and test-only fixture identity preserve the security boundary without new infrastructure.

### Post-design gate: PASS

The resulting model, contract, and test plan retain the above constraints. No constitution exception or added complexity is required.

## Project Structure

```text
src/main/java/com/hospital/smartqueue/
├── appointment/{api,application,domain,infrastructure}/
├── common/security/         # Staff identity port and fail-closed production adapter
└── common/infrastructure/   # Audit extension accepting trusted actor reference

src/main/resources/db/migration/
└── V{next}__create_appointments.sql

src/test/java/com/hospital/smartqueue/
├── appointment/
└── support/                 # Test-only trusted staff identity fixture/configuration

specs/005-appointment-booking/
├── research.md
├── data-model.md
├── quickstart.md
└── contracts/appointment-api.md
```

**Structure Decision**: Extend the existing Java modular monolith. The appointment application depends only on explicit patient, scheduling, audit, and staff-identity contracts; it does not access another feature's repositories directly.

## Complexity Tracking

No constitution violations or additional infrastructure.
