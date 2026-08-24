# Dynamic ETA Validation Guide

## Prerequisites

Integrate authoritative appointment schedule/slot duration, consultation timing, branch timezone, trusted identity, audit, and queue-consumer contracts.

## Validation

1. Seed 10:00, 10:15, and 10:30 appointments with 15-minute slots.
2. Record 10:25 actual completion for 10:00 and assert predictions of 10:25 and 10:40.
3. Verify a scheduled idle gap absorbs delay and no prediction moves before schedule.
4. Advance server clock through an active overrun and assert one-minute prediction refreshes.
5. Exercise concurrent reads/timing updates, scope denial, audit threshold, and persistence rollback.

Run focused ETA tests, then `mvn test` and `git diff --check`.
