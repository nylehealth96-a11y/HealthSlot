import { describe, expect, it } from 'vitest'
import { ApiError } from '../api/apiError'
import { capabilities } from '../api/capabilities'
import { getTrustedIdentity } from '../auth/identity'
import { TEST_ONLY_IDENTITY_LABEL } from '../auth/TestOnlyIdentity'
import { isPending } from '../app/WorkflowState'
import { createPendingAction } from '../app/pendingAction'
import { ApiClient } from '../api/ApiClient'

describe('frontend foundation', () => {
  it('keeps unavailable workflows explicitly disabled', () => {
    expect(Object.values(capabilities).every((capability) => !capability.enabled)).toBe(true)
    expect(Object.values(capabilities).every((capability) => capability.explanation.length > 0)).toBe(true)
  })

  it('uses a clearly labelled development-only identity', () => {
    expect(getTrustedIdentity().label).toBe(TEST_ONLY_IDENTITY_LABEL)
  })

  it('identifies pending workflow actions', () => {
    expect(isPending('loading')).toBe(true)
    expect(isPending('idle')).toBe(false)
  })

  it('preserves standard API error categories', () => {
    expect(new ApiError('CONFLICT', 'Conflict', 409).code).toBe('CONFLICT')
  })

  it('prevents duplicate mutation dispatches', async () => {
    const guard = createPendingAction(); let calls = 0; let release!: () => void
    const pending = new Promise<void>((resolve) => { release = resolve })
    const first = guard.run(async () => { calls += 1; await pending })
    const second = guard.run(async () => { calls += 1 })
    expect(await second).toBeUndefined(); release(); await first; expect(calls).toBe(1)
  })

  it('retries transient read failures but not mutations', async () => {
    let reads = 0
    const readClient = new ApiClient(async () => { reads += 1; if (reads < 3) throw new Error('temporary'); return new Response('{"ok":true}', { status: 200 }) })
    await expect(readClient.request('/health', undefined, { readOnly: true, timeoutMs: 100 })).resolves.toEqual({ ok: true })
    expect(reads).toBe(3)
    let writes = 0
    const writeClient = new ApiClient(async () => { writes += 1; throw new Error('temporary') })
    await expect(writeClient.request('/write', { method: 'POST' })).rejects.toThrow('temporary')
    expect(writes).toBe(1)
  })
})
