# Research: Hospital Structure Management

## Decisions

### Architecture

**Decision**: One Java 17 Spring Boot deployable organized by hospital, department, doctor, and
common features with API, application, domain, and infrastructure responsibilities.

**Rationale**: Meets the modular-monolith constitution without distributed-system overhead.

**Alternatives considered**: Java 21, global technical-layer packages, and microservices were
rejected.

### Persistence and integrity

**Decision**: PostgreSQL is the sole source of truth; immutable, ordered Flyway SQL migrations
create UUID-keyed tables, foreign keys, constraints, and indexes. Use Java `Instant` and
PostgreSQL `timestamptz` in UTC.

**Rationale**: Ensures repeatable evolution and unambiguous audit time.

**Alternatives considered**: Hibernate auto-update, numeric IDs, local timestamps, and caches.

### Ownership and duplicates

**Decision**: Branches reference hospitals, departments reference branches, and doctors reference
hospitals. Application services verify each ownership chain; repository queries scope every
read/write by hospital. Hospital names and professional registration numbers are globally unique;
branch names and doctor codes are unique per hospital; department names are unique per branch.
Duplicate-sensitive values are trimmed and lowercased with `Locale.ROOT`; database uniqueness is
the final concurrent-request authority.

**Rationale**: It prevents both unrelated-ID access and case/whitespace duplicate records.

**Alternatives considered**: Client-trusted IDs, unscoped reads, exact-only comparison, and
application-only duplicate checks.

### Lifecycle, audit, and tests

**Decision**: Doctors use `ACTIVE`/`INACTIVE`, with idempotent changes that retain memberships.
Persist append-only audit events for creation and status changes. Use DTO validation, transactional
services, global exception advice, unit tests for rules, and PostgreSQL Testcontainers integration
tests for migrations, constraints, and error mapping.

**Rationale**: Preserves history and verifies critical rules against PostgreSQL semantics.

**Alternatives considered**: Hard deletion, log-only audit, controller-owned rules, and H2-only
tests.
