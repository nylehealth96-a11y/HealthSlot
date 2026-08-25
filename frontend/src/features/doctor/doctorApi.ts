import type { Capability } from '../../api/capabilities'

export type QueueEntrySummary = { queueEntryId: string; queueNumber: string; status: string; scheduledAt?: string; timezone?: string }
export type ConsultationSummary = { consultationId: string; status: string; startedAt?: string; endedAt?: string; timezone?: string }
export const doctorCapabilities: Capability[] = ['doctorQueue', 'consultation']
