import { describe, expect, it } from 'vitest'
import { capabilities } from '../api/capabilities'
import { patientCapabilities } from '../features/patient/patientApi'

describe('patient workspace contract gate', () => {
  it('keeps absent patient APIs disabled', () => expect(patientCapabilities.every((key) => !capabilities[key].enabled)).toBe(true))
})
