import type { ReactNode } from 'react'
import { TEST_ONLY_IDENTITY_LABEL } from '../auth/TestOnlyIdentity'

export function RoleWorkspace({ title, children }: { title: string; children: ReactNode }) {
  return <section aria-labelledby="workspace-title"><p className="identity">{TEST_ONLY_IDENTITY_LABEL}</p><h2 id="workspace-title">{title}</h2>{children}</section>
}
