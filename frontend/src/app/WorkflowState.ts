export type WorkflowState = 'idle' | 'loading' | 'success' | 'error'
export const isPending = (state: WorkflowState): boolean => state === 'loading'
