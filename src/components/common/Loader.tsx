export default function Loader({ label = 'Loading…' }: { label?: string }) {
  return (
    <div className="flex items-center justify-center gap-3 py-12 text-ledger-500" role="status" aria-live="polite">
      <span className="h-5 w-5 animate-spin rounded-full border-2 border-ledger-200 border-t-ledger-500" />
      <span className="text-sm">{label}</span>
    </div>
  )
}
