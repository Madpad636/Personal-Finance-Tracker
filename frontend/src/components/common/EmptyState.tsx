interface EmptyStateProps {
  title: string
  description?: string
  action?: React.ReactNode
}

export default function EmptyState({ title, description, action }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-2 rounded-lg border border-dashed border-ledger-100 bg-white/50 py-16 text-center">
      <p className="font-display text-lg text-ink">{title}</p>
      {description && <p className="max-w-sm text-sm text-ink/60">{description}</p>}
      {action}
    </div>
  )
}
