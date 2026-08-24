# Research: Appointment Notifications

## Decision: Provider-neutral mock boundary

Use an application-owned `NotificationDeliveryProvider` port with provider-neutral request and result values. Implement only a clearly named mock adapter in test/local contexts.

**Rationale**: It proves business rules without vendor lock-in, credentials, templates, or external delivery effects.

**Alternatives considered**: WhatsApp, SMS, Firebase, or email integration was rejected as outside scope.

## Decision: Idempotent intent before asynchronous delivery

Persist intent with a unique source-event ID and notification type; claim delivery atomically. Repeated events, retries, and concurrent workers use the same intent, and a delivered intent is terminal.

**Rationale**: This prevents duplicate successful delivery and permits recovery.

**Alternatives considered**: In-memory de-duplication was rejected as non-durable and unsafe under concurrency.

## Decision: Bounded retry and audit lifecycle

Allow one initial delivery attempt and at most three retries (four total). Persist attempts and terminal failure; audit intent creation, attempts, outcomes, manual retry, and suppression using IDs, type, status, and reason codes only.

**Rationale**: The policy is testable, recoverable, and avoids unbounded work or patient-data exposure.

**Alternatives considered**: Infinite retries and synchronous workflow delivery were rejected.

## Decision: Fail-closed access and intentional inbound ports

Production status/retry access denies requests until real server-side identity exists. Inbound triggers arrive only through contracts owned by appointment, queue/check-in, ETA/consultation, and scheduling modules.

**Rationale**: Normal request input cannot establish authority, recipient data, or source state.
