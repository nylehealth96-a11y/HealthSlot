# Research: Doctor Consultation Lifecycle

## Decision: Model lifecycle transitions as guarded compare-and-set updates

**Rationale**: `WAITING → CALLED → IN_CONSULTATION → COMPLETED` is a single ordered sequence. A versioned consultation record plus a transaction makes duplicate or out-of-order requests fail safely and leaves one authoritative timestamp per successful start/end transition.

**Alternatives considered**:

- Application-only pre-checks — rejected because concurrent requests can both observe the same prior state.
- A free-form status update — rejected because it allows invalid or skipped transitions.

## Decision: Keep authority in a trusted staff-context port

**Rationale**: Reception may call; only the assigned doctor may start or complete. A server-side trusted context supplies staff identity, roles, and hospital/branch scope. Normal request bodies and paths are never an authority source; production fails closed until a real authentication adapter exists.

**Alternatives considered**:

- Actor/role fields in requests — rejected because callers can forge them.
- Reusing a test identity in production — rejected because it is not authentication.

## Decision: Treat check-in and queue as upstream contracts

**Rationale**: Check-in is the sole source of `WAITING`; queue is the owner of recalculation. Consultation must validate the queue-entry source and request recalculation through explicit interfaces without reading or writing their repositories.

**Alternatives considered**:

- Creating waiting consultations directly — rejected because it bypasses check-in and risks duplicate active visits.
- Copying queue-entry tables into consultation — rejected because it violates domain ownership.

## Decision: Persist transition, audit, and completion hand-off in one consistency boundary

**Rationale**: The lifecycle record and audit record can share the local transaction. If queue recalculation is not in the same transaction, persist an outbox hand-off in that transaction and make recovery idempotent. A failed hand-off cannot leave a silently completed consultation with no eventual queue update.

**Alternatives considered**:

- Best-effort asynchronous recalculation — rejected because it permits silent partial success.
- Cross-module repository transaction — rejected because it breaks module boundaries.

## Decision: Store lifecycle times as instants and render in branch timezone

**Rationale**: Actual start/end are unambiguous instants. The branch timezone is needed only when operational time is displayed or a queue calculation needs local context.

**Alternatives considered**:

- Server-local times — rejected because they are ambiguous across branches and deployments.
