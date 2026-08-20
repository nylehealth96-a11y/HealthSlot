# Implementation Plan: Patient Registration

**Branch**: `002-patient-registration` | **Date**: 2026-08-20 | **Spec**: [spec.md](spec.md)

## Summary

Add a patient feature to the Java 17 Spring Boot modular monolith. Use UUID primary identifiers,
globally unique human-readable patient numbers, Flyway migrations, validated DTOs, transactional
application services, scoped audit events, and `/api/v1` REST endpoints.

## Technical Context

**Language/Version**: Java 17
**Primary Dependencies**: Existing Spring Web, Data JPA, Validation, Flyway, PostgreSQL, Testcontainers
**Storage**: PostgreSQL with Flyway migrations
**Testing**: JUnit 5 and PostgreSQL Testcontainers integration tests
**Project Type**: Backend modular monolith
**Constraints**: DTO-only controllers; mobile is not a primary key; no appointment booking, auth,
frontend, Redis, Kafka, or WebSockets.

## Constitution Check

**PASS**: The feature uses feature-owned `patient` packages, Java 17, PostgreSQL/Flyway, UUIDs,
validated `/api/v1` APIs, audit records, safe logging, and automated tests. No exception needed.

## Project Structure

```text
src/main/java/com/hospital/smartqueue/patient/{api,application,domain,infrastructure}/
src/main/resources/db/migration/V2__create_patients.sql
src/test/java/com/hospital/smartqueue/patient/
```

## Complexity Tracking

No added complexity.
