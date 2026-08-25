import { createPendingAction } from '../../app/pendingAction'

export function createDoctorTransition(refresh: () => Promise<void>) {
  const guard = createPendingAction()
  return { get pending() { return guard.pending }, submit: (action: () => Promise<void>) => guard.run(async () => { await action(); await refresh() }) }
}
