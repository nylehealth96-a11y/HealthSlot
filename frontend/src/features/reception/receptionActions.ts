import { createPendingAction } from '../../app/pendingAction'

export function createReceptionMutation(refresh: () => Promise<void>) {
  const guard = createPendingAction()
  return { get pending() { return guard.pending }, submit: (action: () => Promise<void>) => guard.run(async () => { await action(); await refresh() }) }
}
