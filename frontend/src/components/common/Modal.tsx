import type { ReactNode } from 'react'

interface ModalProps {
  open: boolean
  title: string
  onClose: () => void
  children: ReactNode
}

export default function Modal({ open, title, onClose, children }: ModalProps) {
  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-ink/40 p-4">
      <div className="w-full max-w-md rounded-lg bg-white p-6 shadow-xl">
        <div className="mb-4 flex items-center justify-between">
          <h3 className="font-display text-lg text-ink">{title}</h3>
          <button onClick={onClose} className="text-ink/40 hover:text-ink" aria-label="Close">
            ✕
          </button>
        </div>
        {children}
      </div>
    </div>
  )
}
