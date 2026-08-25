import { describe, expect, it } from 'vitest'
import { capabilities } from '../api/capabilities'
import { doctorCapabilities } from '../features/doctor/doctorApi'

describe('doctor workspace contract gate', () => {
  it('keeps absent queue and consultation APIs disabled', () => expect(doctorCapabilities.every((key) => !capabilities[key].enabled)).toBe(true))
})
