# Appointment Notifications Validation Guide

## Prerequisites

Integrate appointment lifecycle, patient-contact, queue/check-in, consultation/ETA, branch-timezone, and trusted-identity contracts. Configure only the mock provider locally.

## Validation

1. Submit an eligible booking trigger and verify one PII-free pending/delivered patient notification.
2. Repeat its trigger concurrently and verify one intent and no more than one successful delivery.
3. Verify a reminder triggers 24 hours before branch-local appointment time.
4. Verify delay/nearly-due go to the patient; patient-called goes only to in-scope reception.
5. Reschedule and verify an unsent old reminder is suppressed.
6. Fail the mock provider four total attempts and verify terminal failure/audit without source-workflow rollback.
7. Verify out-of-scope status/retry disclose nothing.
8. Run focused notification tests, `mvn test`, and `git diff --check`.
