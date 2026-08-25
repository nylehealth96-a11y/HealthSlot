import type { Capability } from '../../api/capabilities'

export type PatientRegistrationResult = { patientId: string; patientNumber: string }
export type CheckInResult = { queueEntryId: string; queueNumber: string; status: string }
export const receptionCapabilities: Capability[] = ['patientRegistration', 'patientBooking', 'checkIn', 'walkIn']
