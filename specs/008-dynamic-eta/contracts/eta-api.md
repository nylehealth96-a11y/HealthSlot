# Dynamic ETA API Contract

`GET /api/v1/hospitals/{hospitalId}/branches/{branchId}/doctors/{doctorId}/etas` returns the current in-scope prediction version: doctor ID, branch ID, current delay, version, and operational appointment ID with scheduled/predicted start. Patient contact, clinical content, and staff identity are excluded.

Path values locate resources only. Trusted server-side identity supplies role and scope; production fails closed without real authentication. Unknown and out-of-scope resources return the same non-disclosing `404`; malformed input returns `400`; unavailable authoritative schedule/timing/timezone returns `409` without patient data.

Recalculation is internal-only: normal requests do not accept actual timing, delay, override, staff, role, or authorization scope.
