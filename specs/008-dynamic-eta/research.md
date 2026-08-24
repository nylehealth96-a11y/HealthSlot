# Research: Dynamic Doctor ETA

## Decision: Use recursive scheduled-order prediction

**Rationale**: Each prediction is `max(scheduled start, prior predicted completion)`, preserving scheduled gaps and ensuring no ETA moves earlier than schedule.

**Alternatives considered**: Applying one flat delay to every appointment was rejected because it cannot absorb idle gaps.

## Decision: Use authoritative timing and one-minute server refresh

**Rationale**: Consultation actual start/end and server clock are authoritative; caller-provided delays are unsafe. A one-minute active-overrun trigger keeps ETAs current.

**Alternatives considered**: Manual overrides and client-driven refresh were rejected.

## Decision: Persist immutable ETA versions atomically with audit

**Rationale**: A single version gives readers a consistent view under concurrent recalculations. Required audits commit with the published version.

**Alternatives considered**: Updating individual appointments in place was rejected because readers could see partial results.
