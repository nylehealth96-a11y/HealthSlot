export type ApiErrorCode = 'BAD_REQUEST' | 'NOT_FOUND' | 'CONFLICT' | 'UNEXPECTED'

export class ApiError extends Error {
  constructor(public readonly code: ApiErrorCode, message: string, public readonly status?: number) {
    super(message)
    this.name = 'ApiError'
  }
}
