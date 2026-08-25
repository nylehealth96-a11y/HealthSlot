# Frontend View Model

## Role Workspace

Role, permitted navigation, capability map, and non-sensitive state. The role comes from the trusted development adapter only and is replaced by authenticated server identity later.

## Workflow State

`idle`, `loading`, `success`, `empty`, `validation-error`, `conflict`, `unavailable`, or `denied`. Pending actions disable their submit control.

## API Result

Minimal API-provided identifiers, labels, timestamps, and presentation values. The client does not persist patient contact, clinical content, or authorization scope.

## Invariants

- Missing APIs have no enabled action.
- A pending mutation can issue at most one request.
- Server results determine displayed state and timing.
