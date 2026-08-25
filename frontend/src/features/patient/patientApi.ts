import type { Capability } from '../../api/capabilities'

export type DoctorSummary = { doctorId: string; displayName: string; specialty?: string }
export type SlotSummary = { slotId: string; startsAt: string; endsAt: string; timezone: string }
export type AppointmentSummary = { appointmentId: string; appointmentNumber: string; status: string; startsAt: string; timezone: string }
export const patientCapabilities: Capability[] = ['patientSearch', 'patientBooking', 'patientEta']
