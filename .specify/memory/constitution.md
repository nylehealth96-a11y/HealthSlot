<!--
Sync Impact Report
- Version change: 1.0.0 -> 2.0.0
- Modified principles:
  - I. Modular Monolith and Explicit Domain Boundaries -> I. Java Modular Monolith and Domain Boundaries
  - II. Authoritative Data, Identity, and Time -> II. PostgreSQL Authority, Identity, and Time
  - III. Safe Scheduling and Controlled Operational State -> III. Concurrency-Safe Scheduling and Controlled State
  - IV. Secure, Validated, and Auditable Access -> IV. Versioned, Secure, and Auditable APIs
  - V. Focused, Testable MVP Delivery -> V. Testable, Maintainable Delivery
- Added sections: none
- Removed sections: none
- Follow-up TODOs: RATIFICATION_DATE requires the project owner's original adoption date.
-->
# HealthSlot Constitution

## Core Principles

### I. Java Modular Monolith and Domain Boundaries
The backend MUST use Java 17, Spring Boot, and Maven and MUST begin as a modular monolith.
Code MUST be organized by business feature/domain; each module owns its API, application,
domain, and persistence concerns. Global controller, service, or repository package structures
are prohibited. Controllers MUST remain thin transport adapters, and business rules MUST live in
application or domain services. Microservices and infrastructure such as Kafka, Kubernetes,
Elasticsearch, or Redis MUST NOT be introduced without a documented, approved requirement.

Rationale: explicit domain ownership and a small operational footprint keep the platform
maintainable while it evolves.

### II. PostgreSQL Authority, Identity, and Time
PostgreSQL MUST be the primary database and source of truth. Every schema change MUST be a
version-controlled Flyway migration reviewed with the code that depends on it. Internal entity
identifiers MUST use UUIDs. Persisted timestamps MUST preserve the instant in time, and all
user-facing scheduling calculations MUST use explicitly modeled hospital or branch timezone
information rather than the server default.

Rationale: authoritative, migratable data and unambiguous time handling are essential for
reliable multi-location appointment operations.

### III. Concurrency-Safe Scheduling and Controlled State
Appointment booking MUST prevent double booking under concurrent requests through database
constraints and transactional logic; application-only availability checks are insufficient.
Appointment and queue state transitions MUST be explicitly defined, validated, and enforced.
Queue outcomes MUST be deterministic, reproducible from persisted inputs, and testable.

Rationale: availability and queue state directly affect patient access and hospital operations.

### IV. Versioned, Secure, and Auditable APIs
REST endpoints MUST be versioned beneath `/api/v1`. All external input MUST be validated at the
API boundary and wherever domain invariants require it. The application MUST expose consistent
global API error responses. Passwords, tokens, database credentials, JWT secrets, and API keys
MUST NEVER be committed; secrets MUST be supplied through environment variables or external
configuration. Security-sensitive and operationally important actions MUST be audited. Logs MUST
avoid unnecessary medical or personally identifiable health information.

Rationale: consistent contracts, secure handling of secrets, and accountable operations reduce
risk without exposing unnecessary patient information.

### V. Testable, Maintainable Delivery
All important business rules MUST have automated tests, including booking concurrency and
appointment and queue transitions. Changes MUST remain simple, maintainable, and testable.
Architecture exceptions or new infrastructure require a documented rationale, alternatives
considered, an owner, and a review or removal date.

Rationale: focused verification and deliberate complexity keep a healthcare workflow dependable
as requirements change.

## Platform & Data Constraints

Backend modules MUST expose only intentional contracts to other modules and MUST NOT access
another module's persistence implementation directly. PostgreSQL migrations MUST be forward
compatible where practical. Authorization decisions MUST consider role, hospital, branch, and
resource ownership where applicable. Audit records MUST be retained in PostgreSQL unless a
separate approved requirement defines another authoritative store.

## Delivery & Quality Requirements

Every change MUST identify affected domains, validation rules, authorization scope, audit impact,
and database-migration impact. Reviews MUST verify that controllers have not absorbed business
logic; verify that errors use the global response format; and verify automated coverage for
important affected rules. Feature plans, code reviews, and release-readiness reviews MUST record
any approved constitution exception.

## Governance

This constitution supersedes conflicting implementation conventions. Amendments require a
documented proposal, rationale, impact on existing modules and migrations, and maintainer
approval. A MAJOR version removes or incompatibly redefines a governing principle; a MINOR
version adds a principle or materially expands governance; a PATCH version clarifies wording
without changing policy. Each specification, plan, task set, implementation review, and release
readiness review MUST assess compliance with this constitution and record approved exceptions.

**Version**: 2.0.0 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date is unknown | **Last Amended**: 2026-08-20
