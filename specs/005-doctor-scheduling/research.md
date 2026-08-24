# Research: Doctor Scheduling

## Decisions

### Branch-scoped schedules

- **Decision**: Key schedules, leave, exceptions, and availability by `(doctorId, branchId)`.
- **Rationale**: Doctors currently connect to departments, and departments connect to branches; a doctor may therefore serve more than one branch. This supplies the selected branch timezone without redefining the existing doctor model.
- **Alternatives considered**: Adding one `branch_id` to `doctors` would break the current multi-department model. Using hospital timezone contradicts the clarified branch-timezone decision.

### Time representation

- **Decision**: Add a required IANA `timezone` to branches. Store schedule wall-clock values as `LocalTime`, calendar rules as `LocalDate`/day-of-week, and audit timestamps as `Instant`; derive slot instants by applying the branch `ZoneId`.
- **Rationale**: User-facing schedule rules are local to a branch, while persisted events remain unambiguous UTC instants.
- **Alternatives considered**: Server timezone is prohibited by the constitution. Fixed offsets fail daylight-saving transitions.

### Effective-dated recurring revision

- **Decision**: Persist immutable recurring revisions with `effective_from`; select the latest revision whose date is on or before the requested date.
- **Rationale**: It preserves historic derivation and satisfies the selected effective-date behavior.
- **Alternatives considered**: Updating one mutable schedule destroys history. A separate end-date is unnecessary because the next revision supplies the boundary.

### Stale-write protection

- **Decision**: Use an optimistic `version` token on mutable schedule-rule resources and return a `STALE_REVISION` conflict for mismatches.
- **Rationale**: It prevents silent overwrites while retaining simple transactions.
- **Alternatives considered**: Last-write-wins loses changes. Automatic merging cannot safely infer intent for schedule intervals.

### Availability calculation

- **Decision**: Do not persist slots. Build them for each requested local date using precedence `leave > exception > effective recurring revision`, then subtract breaks and drop incomplete final slots.
- **Rationale**: The result remains deterministic from authoritative rules and has no reservation semantics.
- **Alternatives considered**: Persisted slots create synchronization and invalidation work; booking is out of scope.

### Security boundary before real authentication

- **Decision**: Scheduling application services depend on a feature-owned `SchedulingAccessContext` port. The production adapter always fails closed; `TrustedTestSchedulingAccessContext` exists only under test sources/configuration.
- **Rationale**: This meets the current test-only trusted-context constraint and allows real authentication to be swapped in without rewriting business logic.
- **Alternatives considered**: Reading actor, role, hospital, branch, or authorization scope from headers/body/query is prohibited. Leaving endpoints open is unsafe.

### Errors and auditing

- **Decision**: Extend global error mapping for unauthenticated/forbidden access and schedule stale conflicts. Record schedule mutations through the existing audit store with trusted staff reference and PII-minimal metadata.
- **Rationale**: Existing domain errors otherwise map non-not-found cases to 409, which is not suitable for identity failures.
- **Alternatives considered**: Auditing as `SYSTEM` prevents accountability; detailed schedule payloads in audit metadata add unnecessary operational data.
