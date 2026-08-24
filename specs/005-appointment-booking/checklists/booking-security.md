# Booking Safety and Security Checklist: Appointment Booking

**Purpose**: Deep PR-review requirements-quality checklist for booking lifecycle safety, concurrency, authorization, audit, and dependency boundaries.
**Created**: 2026-08-24
**Feature**: [spec.md](../spec.md)

**Note**: This custom checklist is generated from the feature requirements and design artifacts.
**Review Ownership**: This checklist is a reviewer-owned requirements-quality review artifact. Mark an item `[x]` only when the reviewer determines the requirements-quality criterion is satisfied.
**Marker Semantics**: `[x]` means the criterion has been reviewed and satisfied for requirements quality. It does not mean implementation work is complete.

## Requirement Completeness

- [x] CHK001 Are required appointment attributes, ownership relationships, immutability rules, and state values fully specified? [Evidence: Spec §Key Entities; Data Model §Appointment]
- [x] CHK002 Are the required conditions for a requested interval to qualify as an available slot fully specified, including schedule, branch, and active-reservation inputs? [Evidence: Spec §FR-001, FR-006, FR-016; Data Model §Derived available slot]
- [x] CHK003 Are the requirements for globally unique, human-readable appointment numbers sufficiently specific to distinguish operational readability from uniqueness? [Evidence: Spec §FR-003; Data Model §Appointment]
- [x] CHK004 Are cancellation and rescheduling reason requirements complete about optionality, retention, and exclusion of unnecessary personal information? [Evidence: Spec §FR-013; Edge Cases]
- [x] CHK005 Are read requirements complete about the appointment fields disclosed to authorized staff and the treatment of cancelled appointments? [Evidence: Spec §User Story 3; FR-010; Contracts §Read appointment]

## Lifecycle and Consistency

- [x] CHK006 Are all allowed and disallowed appointment status transitions explicitly defined and consistent between user stories, edge cases, and functional requirements? [Evidence: Spec §User Story 2, §Edge Cases, FR-004, FR-007, FR-011; Data Model §Appointment]
- [x] CHK007 Is the rule that changes are allowed only before the appointment start expressed unambiguously for both cancellation and rescheduling? [Evidence: Spec §Clarifications, §Edge Cases, FR-011]
- [x] CHK008 Are the current-version requirements consistent across cancellation, rescheduling, stale-change handling, and response expectations? [Evidence: Spec §Clarifications, §Edge Cases, FR-011; Contracts §§Cancel appointment, Reschedule appointment]
- [x] CHK009 Does the specification define whether a reschedule request targeting the unchanged interval is an allowed no-op or an invalid change? [Evidence: Spec §Edge Cases, FR-008, FR-011; Contracts §Reschedule appointment]
- [x] CHK010 Are appointment-number immutability and retention during cancellation/rescheduling consistently stated across requirements and data-model assumptions? [Evidence: Spec §FR-003, FR-008; Data Model §Appointment]

## Concurrency and Recovery Requirements

- [x] CHK011 Are concurrent booking requirements precise about the contested resource, the expected single winner, and the losing request’s conflict outcome? [Evidence: Spec §User Story 1, FR-005, SC-002]
- [x] CHK012 Are requirements defined for overlapping intervals as well as identical start/end times for the same doctor and branch? [Evidence: Spec §FR-005; Data Model §Persistence invariants]
- [x] CHK013 Are requirements clear that availability information may become stale and cannot guarantee a subsequent booking outcome? [Evidence: Spec §Edge Cases; FR-006]
- [x] CHK014 Is the atomic failed-reschedule recovery outcome fully specified, including preservation of original status, interval, and appointment number? [Evidence: Spec §FR-008, FR-009, §Edge Cases, SC-004]
- [x] CHK015 Are conflict categories sufficiently differentiated for unavailable slots, stale versions, invalid state transitions, and concurrent reservations? [Evidence: Spec §FR-006, FR-011; Contracts §Error behavior]
- [x] CHK016 Are requirements stated for concurrent cancellation/reschedule attempts against the same appointment version? [Evidence: Spec §Edge Cases, FR-011]

## Authorization, Privacy, and Audit

- [x] CHK017 Are required scheduling or reception roles and their allowed appointment actions explicitly defined? [Evidence: Spec §Appointment Permission, FR-015; Plan §Technical Context]
- [x] CHK018 Are hospital and branch scope rules consistent for availability, booking, retrieval, cancellation, and rescheduling? [Evidence: Spec §FR-001, FR-002, FR-007, FR-008, FR-010, FR-014, FR-015]
- [x] CHK019 Is the non-disclosure requirement sufficiently clear about whether each out-of-scope resource operation returns the same outcome as a missing resource? [Evidence: Spec §User Story 3, SC-005, FR-015; Contracts §Error behavior]
- [x] CHK020 Are the fail-closed production-identity requirements explicit that no normal input channel can establish actor, role, hospital, branch, or scope? [Evidence: Spec §FR-014; Plan §Technical Context; Contracts preamble]
- [x] CHK021 Are requirements clear that the trusted test identity is isolated from production behavior and cannot become a runtime authentication path? [Evidence: Spec §Assumptions; Data Model §Trusted staff identity port; Plan §Constitution Check]
- [x] CHK022 Are audit requirements specific enough to identify required action, trusted actor, target, scope, optional reason, and PII exclusions? [Evidence: Spec §FR-013; Data Model §Appointment audit event]

## Time, Availability, and Dependencies

- [x] CHK023 Are branch-local date-range, timezone, daylight-saving, and persisted-instant requirements consistent and objectively interpretable? [Evidence: Spec §FR-001, FR-012, §Edge Cases; Research §Time and availability; Data Model §Derived available slot]
- [x] CHK024 Is the 31-calendar-day range rule precise about inclusive boundaries and invalid `from`/`to` ordering? [Evidence: Spec §Edge Cases, FR-001; Contracts §Availability]
- [x] CHK025 Are slot-duration requirements complete about exact matching, adjacent intervals, and any schedule-derived rounding convention? [Evidence: Spec §FR-005, FR-006; Data Model §§Appointment, Persistence invariants; Research §Availability ownership]
- [x] CHK026 Are required patient, doctor, branch, timezone, and scheduling dependencies documented with ownership and failure behavior when prerequisite data is absent or inconsistent? [Evidence: Spec §Assumptions, FR-016; Plan §Structure Decision; Tasks T001, T009]
- [x] CHK027 Are requirements clear that appointment booking consumes scheduling-derived availability without redefining recurring schedules, leave, breaks, or exceptions? [Evidence: Spec §Assumptions; Research §Availability ownership]

## Acceptance and Operational Quality

- [x] CHK028 Can each primary booking, cancellation, rescheduling, and retrieval acceptance scenario be objectively evaluated from the stated outcomes? [Evidence: Spec §User Scenarios & Testing; SC-002, SC-004, SC-005]
- [x] CHK029 Are success criteria clear about the workload, timing boundary, and measurement method for 95% latency targets? [Evidence: Spec §SC-001, SC-003; Tasks T037]
- [x] CHK030 Are requirements defined for authorization/audit failure handling without exposing personal or scheduling information? [Evidence: Spec §FR-013, FR-014, FR-015; Contracts §Error behavior]
- [x] CHK031 Are requirements defined for operational observability of booking conflicts and fail-closed identity failures while excluding PII? [Evidence: Spec §FR-017; Tasks T037]

## Notes

- Mark items `[x]` only after review confirms the requirements-quality criterion is satisfied.
- Leave items unchecked when they still require clarification, correction, or reviewer evaluation.
- `$speckit-implement` reads checklist checkbox state as a gate and must not modify markers.
- `checklists/requirements.md` has a separate built-in lifecycle maintained by `$speckit-specify` and `$speckit-clarify`.
- Items are numbered sequentially for reviewer discussion and PR traceability.
