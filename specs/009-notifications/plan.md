# Implementation Plan: Appointment Notifications

**Branch**: `009-notifications` | **Date**: 2026-08-24 | **Spec**: [spec.md](./spec.md)

## Summary

Create a notification module that records idempotent notification intents from authoritative workflow events and delivers them asynchronously through a provider-neutral boundary. A clearly named mock provider is the only implementation in this feature; real communications vendors are excluded.

## Technical Context

**Language/Version**: Java 17; **Dependencies**: Spring Boot, JPA, Flyway, validation, JUnit/Testcontainers; **Storage**: PostgreSQL UUID records and instants; **Testing**: unit, API, persistence, concurrency, failure-injection; **Project**: modular-monolith REST service.

**Constraints**: A unique `(source_event_id, notification_type)` intent prevents duplicate delivery. One initial attempt plus no more than three retries is permitted. Contact destinations and rendered content are transient and excluded from logs/status APIs. Trigger identity, destination, appointment state, and scope come only from server-side upstream ports. Production access remains fail-closed until real authentication exists. Notification intent is transactionally recorded with its source event where an upstream workflow offers that boundary; delivery is asynchronous and never rolls back the source workflow.

**Scale/Scope**: Validate status lookup over 50 records within 2 seconds locally; support only booking, 24-hour reminder, doctor-delay, nearly-due, patient-called, cancellation, and reschedule events. WhatsApp, SMS, Firebase, email, notification preferences, message templates, and provider credentials are out of scope.

## Constitution Check

PASS subject to explicit prerequisites. The `notification` module owns its API, application, domain, and persistence. It consumes intentional appointment, patient-contact, queue/check-in, ETA/consultation, branch-timezone, and trusted-identity contracts without direct upstream persistence access. PostgreSQL/Flyway remain authoritative; notification/audit state changes use one transaction. No vendor infrastructure, real provider credentials, or unaudited operational transitions are introduced.

## Project Structure

```text
src/main/java/com/hospital/smartqueue/notification/{api,application,domain,infrastructure}/
src/main/java/com/hospital/smartqueue/notification/application/port/
src/test/java/com/hospital/smartqueue/notification/{api,application,infrastructure}/
src/test/java/com/hospital/smartqueue/support/NotificationFixtures.java
src/main/resources/db/migration/V<next-contiguous>__create_notifications.sql
specs/009-notifications/{research.md,data-model.md,quickstart.md,contracts/}
```

**Structure Decision**: `notification` owns intents, attempts, retry state, and a provider-neutral port. It does not own appointments, queues, consultation timing, patient contacts, timezone, or staff identity. Select the Flyway number only after the integrated migration baseline is known.

## Upstream Integration Prerequisites

| Needed capability | Required server-side contract | Current branch state | Dependency before |
|---|---|---|---|
| Appointment lifecycle | Event ID/revision, appointment scope/state, patient eligibility, atomic source-event boundary | Absent | Booking/cancellation/reschedule/reminder adapter |
| Patient contact | Authorized eligible destination selected server-side | Absent | Patient-bound delivery adapter |
| Queue/check-in | Active visit ID plus nearly-due/patient-called eligibility | Absent | Queue event adapter |
| Consultation/ETA | Doctor-delay event ID/revision and eligibility | Absent | Delay event adapter |
| Branch timezone | Branch-local timezone lookup | Absent | Reminder/recipient timing |
| Trusted identity | Server-side staff identity and scope | Absent | Production status/retry |

## Complexity Tracking

No constitution violations or additional infrastructure are required.
