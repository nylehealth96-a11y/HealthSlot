import { TEST_ONLY_IDENTITY, TEST_ONLY_IDENTITY_LABEL } from './TestOnlyIdentity'

export function getTrustedIdentity() {
  if (import.meta.env.PROD) throw new Error('Production authentication is not configured; trusted test identity is disabled.')
  return { ...TEST_ONLY_IDENTITY, label: TEST_ONLY_IDENTITY_LABEL }
}
