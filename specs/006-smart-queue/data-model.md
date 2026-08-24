# Data Model: Smart Waiting Queue

## QueuePriority

`NORMAL`, `PRIORITY`, `URGENT`; applies only to `WAITING` entries.

## DoctorOperationalStatus

`AVAILABLE`, `BUSY`, `UNAVAILABLE`; scope is doctor and branch.

## QueueProjection

Derived per doctor, branch, and local date: immutable queue number, position, patients ahead, predicted start, estimated wait, priority, projection version, and doctor status. Order is priority, arrival instant, queue reference.
