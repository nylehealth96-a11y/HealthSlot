# Data Model: Dynamic Doctor ETA

## EtaPredictionVersion

UUID ID, doctor/hospital/branch IDs, branch-local service date, source consultation timing revision, calculated-at instant, current delay, version number, and immutable publication status. One current version is visible per doctor/branch/local date.

## UpcomingAppointmentEta

Prediction-version ID, upstream appointment ID, scheduled start instant, configured slot duration, predicted start instant, predicted completion instant, deterministic order key. It contains no patient PII.

## EtaRecalculationTrigger

Authoritative consultation timing revision or one-minute active-overrun tick, doctor/branch scope, source instant, and idempotency key.

## Invariants

- Only upcoming same-day in-scope appointments are included.
- Predicted start is never earlier than scheduled start.
- Publication and required audit are atomic; readers see only a complete version.
- The first delay-caused version and shifts of at least five minutes are audited.
