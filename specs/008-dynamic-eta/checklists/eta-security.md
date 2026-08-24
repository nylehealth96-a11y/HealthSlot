# ETA Security and Accuracy Checklist: Dynamic Doctor ETA

**Purpose**: Deep PR-review checklist for ETA requirement quality across timing, concurrency, privacy, audit, recovery, and upstream contracts.
**Created**: 2026-08-24
**Feature**: [spec.md](../spec.md)

**Note**: This custom checklist is reviewer-owned. `[x]` means requirements quality has been approved, not that implementation is complete.

## Calculation Completeness

- [x] CHK001 Are expected-end and non-negative-delay requirements explicitly defined? [Completeness, Spec §FR-001]
- [x] CHK002 Are appointment ordering and per-appointment duration requirements defined? [Completeness, Spec §FR-002–003]
- [x] CHK003 Is the idle-gap absorption rule consistent with the no-earlier-than-scheduled invariant? [Consistency, Spec §FR-003, Edge Cases]
- [x] CHK004 Are active and completed consultation timing sources distinguished? [Clarity, Spec §FR-004]
- [x] CHK005 Are completed, cancelled, and historical appointment exclusions fully specified? [Completeness, Spec §FR-005]

## Refresh and Concurrency

- [x] CHK006 Is the one-minute active-overrun refresh trigger defined with server-clock authority? [Clarity, Spec §FR-002, FR-004]
- [x] CHK007 Are missed, delayed, and duplicate one-minute trigger requirements defined? [Gap]
- [x] CHK008 Are concurrent timing updates and ETA reads required to use one deterministic version? [Completeness, Spec §FR-006, SC-002]
- [x] CHK009 Are version identity, scope, and replacement/publication semantics specified? [Clarity, Spec §FR-006; Data Model §EtaPredictionVersion]
- [x] CHK010 Are stale source-timing revision and recalculation conflict outcomes specified? [Gap]

## Authorization and Privacy

- [x] CHK011 Are trusted identity, role, hospital, and branch scope requirements defined for ETA retrieval? [Completeness, Spec §FR-007–009]
- [x] CHK012 Is production fail-closed behavior clearly separated from test-only trusted context? [Security, Spec §FR-008, Assumptions]
- [x] CHK013 Are unknown and out-of-scope ETA requests required to have identical non-disclosing outcomes? [Consistency, Spec §FR-008; Contract]
- [x] CHK014 Is the operational response boundary explicit about excluding patient contact and clinical information? [Privacy, Spec §FR-007; Contract]
- [x] CHK015 Are caller-supplied timing, delay, override, and authority values explicitly forbidden? [Security, Spec §FR-009]

## Audit and Recovery

- [x] CHK016 Is the first delay-caused audit version requirement defined unambiguously? [Clarity, Spec §FR-010]
- [x] CHK017 Is the five-minute material-shift threshold consistent for every affected appointment? [Consistency, Spec §FR-010]
- [x] CHK018 Are audit metadata and PII exclusions documented? [Completeness, Spec §FR-010]
- [x] CHK019 Is the atomic prediction/audit/publication boundary defined for all failure cases? [Recovery, Spec §FR-011, SC-004]
- [x] CHK020 Are recovery ownership, retry, and terminal failure visibility requirements defined? [Gap]

## Dependencies and Timezone

- [x] CHK021 Are appointment schedule, slot duration, consultation timing, timezone, identity, audit, and queue contracts specified as intentional boundaries? [Completeness, Assumptions; Plan]
- [x] CHK022 Is branch-local service-date behavior consistent with persisted-instant requirements? [Consistency, Spec §FR-006, Edge Cases]
- [x] CHK023 Are missing or invalid upstream slot-duration, timing, ordering, and timezone outcomes specified? [Completeness, Spec §FR-012]
- [x] CHK024 Are smart-queue consumption boundaries specified without transferring queue ownership? [Completeness, Assumptions]
- [x] CHK025 Is migration sequencing tied to the integrated baseline rather than an assumed version? [Completeness, Plan]

## Acceptance Coverage

- [x] CHK026 Does the Rahul/Amit/Priya example fully define inputs, timezone, duration, and expected outputs? [Measurability, Spec §SC-001]
- [x] CHK027 Are no-delay, idle-gap, active-overrun, completion, and unavailable-source scenarios covered? [Coverage, User Stories, Edge Cases]
- [x] CHK028 Are scope denial, wrong-role, and cross-branch scenarios covered? [Coverage, Spec §FR-008–009, SC-003]
- [x] CHK029 Are concurrency and version-consistency scenarios covered? [Coverage, Spec §SC-002]
- [x] CHK030 Are audit/persistence rollback and recovery scenarios covered? [Coverage, Spec §SC-004]
- [x] CHK031 Are one-minute refresh and performance validation conditions objectively measurable? [Measurability, Spec §FR-002, SC-005]

## Notes

- Mark items `[x]` only after reviewer approval of requirements quality.
- `$speckit-implement` reads checklist state and must not modify markers.
