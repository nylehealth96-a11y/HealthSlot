# Data Model: Appointment Notifications

## NotificationIntent

UUID ID; source-event ID/revision; hospital, branch, appointment/visit reference; type; recipient class (`PATIENT` or `RECEPTION`); status; attempt count; scheduled/created/updated instants; and PII-minimized failure reason. Unique `(source_event_id, notification_type)`.

**States**: `PENDING` → `DELIVERING` → `DELIVERED`; `DELIVERING` → `PENDING` for retry; `DELIVERING` → `FAILED` after the fourth failed total attempt; `PENDING`/`DELIVERING` → `SUPPRESSED` when source is ineligible. `DELIVERED`, `FAILED`, and `SUPPRESSED` are terminal except authorized manual retry of an eligible failed intent.

## DeliveryAttempt

UUID ID; intent ID; sequence; attempted-at instant; provider-neutral outcome; and non-sensitive reason code. Contact destination and rendered body are not persisted here.

## NotificationTrigger

Server-side value supplied by a workflow: source-event ID/revision, type, scoped appointment/visit reference, authoritative recipient selection, eligibility, and branch-local timing context. It is never normal request input.

## Invariants

- A source event/type has at most one intent and one successful delivery.
- Intent and audit record commit atomically; async delivery never rolls back source workflow.
- Attempt count never exceeds four without authorized manual retry.
- Only in-scope trusted identity can view status/retry; unknown and out-of-scope resources are indistinguishable.
- Timestamps are instants; branch timezone controls recipient-facing wording.
