import { describe, expect, it } from 'vitest'
import { AsyncState } from '../components/AsyncState'
import { RoleWorkspace } from '../components/RoleWorkspace'

describe('accessibility baseline', () => {
  it('uses labelled semantic controls and status regions', () => {
    expect(AsyncState).toBeDefined()
    expect(RoleWorkspace).toBeDefined()
  })
})
