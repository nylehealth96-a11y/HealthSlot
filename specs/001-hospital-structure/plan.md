# Implementation Plan: Hospital Structure Management

**Branch**: `001-hospital-structure` | **Date**: 2026-08-21 | **Spec**: [spec.md](spec.md)

## Summary

Build a Java 17 Spring Boot modular-monolith REST service for hospitals, branches, departments,
doctors, memberships, and doctor status. PostgreSQL and Flyway enforce UUID identity, duplicate
prevention, and hospital ownership boundaries. Controllers use validated DTOs; transactional
application services own business rules; global advice produces consistent errors.

## Technical Context

**Language/Version**: Java 17 only
**Primary Dependencies**: Spring Boot, Web, Data JPA, Jakarta Validation, Flyway, PostgreSQL JDBC,
Spring Boot Test, Testcontainers PostgreSQL
**Storage**: PostgreSQL source of truth; version-controlled Flyway SQL migrations
**Testing**: JUnit 5, Spring Boot integration tests, Testcontainers PostgreSQL
**Target Platform**: Server-side web service
**Project Type**: Single backend modular monolith
**Performance Goals**: Meet the acceptance dataset in the specification.
**Constraints**: `/api/v1`; UUID IDs; DTO-only controllers; no JPA entity exposure; no Java 21,
Redis, Kafka, WebSockets, authentication, or frontend.
**Scale/Scope**: Hospital structure and doctor directory only; no deletion, schedules, patients,
appointments, or queues.
**Database Evolution**: Flyway `V1__create_hospital_structure.sql` creates only the foundational
hospital, branch, department, doctor, doctor-department membership, and audit schema.

## Constitution Check

**Pre-design gate: PASS.** Java 17, Maven, Spring Boot, PostgreSQL, Flyway, package-by-feature,
thin controllers, validated versioned APIs, UUIDs, database integrity, auditing, safe logs, and
automated tests are all covered. No prohibited infrastructure is introduced.

**Post-design gate: PASS.** The data model, scoped queries, audit events, contracts, and test
strategy below preserve the constitution. No exception is required.

## Project Structure

### Documentation (this feature)

```text
specs/001-hospital-structure/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/hospital-structure-api.yaml
└── tasks.md                 # Generated later
```

### Source Code (repository root)

```text
pom.xml
src/main/java/com/hospital/smartqueue/
├── common/{api,domain,infrastructure}
├── hospital/{api,application,domain,infrastructure}
├── department/{api,application,domain,infrastructure}
└── doctor/{api,application,domain,infrastructure}
src/main/resources/{application.yml,db/migration/}
src/test/java/com/hospital/smartqueue/{hospital,department,doctor,integration}/
```

**Structure Decision**: One Maven/Spring Boot service with feature-owned packages. `common`
contains only shared cross-cutting contracts and infrastructure.

## Complexity Tracking

No constitution violations or justified complexity additions.
