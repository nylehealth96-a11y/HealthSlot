import type { ApiErrorCode } from './apiError'

export const roleFields = {
  patient: ['appointmentId', 'appointmentNumber', 'status', 'startsAt', 'eta', 'timezone'],
  doctor: ['queueEntryId', 'queueNumber', 'status', 'scheduledAt', 'timezone'],
  reception: ['patientId', 'patientNumber', 'appointmentId', 'queueEntryId', 'queueNumber', 'status'],
} as const

export function mapApiError(code: ApiErrorCode): 'validation' | 'notFoundOrDenied' | 'conflictOrUnavailable' | 'unexpected' {
  if (code === 'BAD_REQUEST') return 'validation'
  if (code === 'NOT_FOUND') return 'notFoundOrDenied'
  if (code === 'CONFLICT') return 'conflictOrUnavailable'
  return 'unexpected'
}

export function allowlisted<T extends Record<string, unknown>>(value: T, fields: readonly string[]): Partial<T> {
  return Object.fromEntries(fields.filter((field) => field in value).map((field) => [field, value[field]])) as Partial<T>
}
