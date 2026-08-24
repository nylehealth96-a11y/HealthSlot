# Research: Smart Waiting Queue

**Decision**: Build a derived queue projection; queue numbers remain immutable while position, patients ahead, predicted start, and estimated wait are recalculated.

**Rationale**: Separates stable operational identity from changing order.

**Decision**: Order `URGENT`, `PRIORITY`, `NORMAL`, then arrival instant, then queue reference.

**Rationale**: It is deterministic, auditable, and implements the agreed priority model.

**Decision**: Use doctor configured slot duration for each available queue estimate; unavailable status returns unavailable estimates.

**Rationale**: Avoids fabricated waiting times.
