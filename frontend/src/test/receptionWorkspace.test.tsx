import { describe, expect, it } from 'vitest'
import { capabilities } from '../api/capabilities'
import { receptionCapabilities } from '../features/reception/receptionApi'

describe('reception workspace contract gate', () => {
  it('keeps absent reception APIs disabled', () => expect(receptionCapabilities.every((key) => !capabilities[key].enabled)).toBe(true))
})
