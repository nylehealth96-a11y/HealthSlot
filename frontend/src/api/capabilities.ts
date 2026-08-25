export type Capability = 'patientSearch' | 'patientBooking' | 'patientEta' | 'doctorQueue' | 'consultation' | 'patientRegistration' | 'checkIn' | 'walkIn'

export type CapabilityState = { enabled: boolean; explanation: string }

const unavailable = (name: string): CapabilityState => ({ enabled: false, explanation: `${name} is unavailable until its backend API contract is validated.` })

export const capabilities: Record<Capability, CapabilityState> = {
  patientSearch: unavailable('Doctor search'), patientBooking: unavailable('Appointment booking'), patientEta: unavailable('ETA'),
  doctorQueue: unavailable("Today's queue"), consultation: unavailable('Consultation actions'), patientRegistration: unavailable('Patient registration'),
  checkIn: unavailable('Check-in'), walkIn: unavailable('Walk-in registration'),
}
