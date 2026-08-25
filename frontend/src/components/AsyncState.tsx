export function AsyncState({ loading, error }: { loading?: boolean; error?: string }) {
  if (loading) return <p role="status" aria-live="polite">Loading…</p>
  if (error) return <p role="alert">{error}</p>
  return null
}
