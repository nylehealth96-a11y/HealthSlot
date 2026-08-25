import { describe, expect, it } from 'vitest'
import { TEST_ONLY_IDENTITY_LABEL } from '../auth/TestOnlyIdentity'

describe('frontend security baseline', () => {
  it('labels local identity as development-only', () => expect(TEST_ONLY_IDENTITY_LABEL).toContain('development only'))
})
