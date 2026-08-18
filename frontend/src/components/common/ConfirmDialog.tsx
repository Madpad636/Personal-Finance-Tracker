interface ConfirmDialogProps {
  open: boolean
  title: string
  message: string
  onConfirm: () => void
  onCancel: () => void
  confirmLabel?: string
}

export default function ConfirmDialog({ open, title, message, onConfirm, onCancel, confirmLabel = 'Confirm' }: ConfirmDialogProps) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-ink/40 p-4">
      <div className="w-full max-w-sm rounded-lg bg-white p-6 shadow-xl">
        <h3 className="font-display text-lg text-ink">{title}</h3>
        <p className="mt-2 text-sm text-ink/70">{message}</p>
        <div className="mt-6 flex justify-end gap-3">
          <button onClick={onCancel} className="rounded-md px-4 py-2 text-sm text-ink/70 hover:bg-ink/5">
            Cancel
          </button>
          <button onClick={onConfirm} className="rounded-md bg-signal-expense px-4 py-2 text-sm text-white hover:opacity-90">
            {confirmLabel}
          </button>
        </div>
      </div>
    </div>
  )
}
