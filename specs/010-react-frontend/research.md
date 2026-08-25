# Research: Initial Role-Based Frontend

## Decision: Thin API-client feature modules

Each workspace owns views, API client calls, pending/error state, and disabled-state rendering; it does not calculate clinical or scheduling decisions.

**Rationale**: Keeps authority in backend domains and makes unavailable APIs safe to represent.

## Decision: Test/local identity boundary

Use a clearly named development-only identity adapter. It is visually labelled and cannot be enabled for production builds.

**Rationale**: Enables UI verification without presenting browser identity as production authorization.

## Decision: Explicit API-capability gating

Maintain a capability map from validated contracts. Missing capability disables its action and shows a non-sensitive explanation.

**Rationale**: Avoids mock success and stale client-side business logic.
