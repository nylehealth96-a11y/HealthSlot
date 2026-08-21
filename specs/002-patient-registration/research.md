# Research: Patient Registration

## Decisions

**Decision**: Generate a globally unique patient number separate from UUID primary identity.
**Rationale**: It gives staff a readable reference without using mobile numbers as identity.
**Alternatives considered**: Mobile-as-key and numeric database IDs were rejected.

**Decision**: Store optional contact fields as nullable and allow all basic details to be updated;
both identifiers are immutable.
**Rationale**: Reception staff can correct data while preserving a stable patient identity.
**Alternatives considered**: Immutable demographics and mandatory optional contacts were rejected.
