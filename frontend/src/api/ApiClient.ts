import { ApiError, type ApiErrorCode } from './apiError'

const API_PREFIX = '/api/v1'

export class ApiClient {
  constructor(private readonly fetcher: typeof fetch = fetch) {}

  async request<T>(path: string, init?: RequestInit, options: { readOnly?: boolean; timeoutMs?: number } = {}): Promise<T> {
    const attempts = options.readOnly ? 3 : 1
    let lastError: unknown
    for (let attempt = 0; attempt < attempts; attempt += 1) {
      const controller = new AbortController()
      const timer = setTimeout(() => controller.abort(), options.timeoutMs ?? 5000)
      try {
        const response = await this.fetcher(`${API_PREFIX}${path}`, { ...init, signal: controller.signal, headers: { Accept: 'application/json', ...init?.headers } })
        clearTimeout(timer)
        if (!response.ok) {
          if (options.readOnly && response.status >= 500 && attempt < attempts - 1) continue
          throw new ApiError(this.codeFor(response.status), await this.messageFor(response), response.status)
        }
        return response.status === 204 ? (undefined as T) : await response.json() as T
      } catch (error) {
        clearTimeout(timer); lastError = error
        if (!(options.readOnly && attempt < attempts - 1 && !(error instanceof ApiError))) throw error
      }
    }
    throw lastError
  }

  private codeFor(status: number): ApiErrorCode {
    if (status === 400) return 'BAD_REQUEST'
    if (status === 404) return 'NOT_FOUND'
    if (status === 409) return 'CONFLICT'
    return 'UNEXPECTED'
  }

  private async messageFor(response: Response): Promise<string> {
    try { const body = await response.json() as { message?: string }; return body.message ?? 'Request failed' } catch { return 'Request failed' }
  }
}
