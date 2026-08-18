import { createContext, useCallback, useContext, useState, type ReactNode } from 'react'

type ToastKind = 'success' | 'error' | 'info'
interface ToastMessage { id: number; kind: ToastKind; text: string }

const ToastContext = createContext<{ notify: (kind: ToastKind, text: string) => void } | undefined>(undefined)

const kindStyles: Record<ToastKind, string> = {
  success: 'bg-ledger-500',
  error: 'bg-signal-expense',
  info: 'bg-ink'
}

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastMessage[]>([])

  const notify = useCallback((kind: ToastKind, text: string) => {
    const id = Date.now()
    setToasts((prev) => [...prev, { id, kind, text }])
    setTimeout(() => setToasts((prev) => prev.filter((t) => t.id !== id)), 4000)
  }, [])

  return (
    <ToastContext.Provider value={{ notify }}>
      {children}
      <div className="fixed bottom-4 right-4 z-50 flex flex-col gap-2">
        {toasts.map((t) => (
          <div key={t.id} className={`rounded-md px-4 py-2 text-sm text-white shadow-lg ${kindStyles[t.kind]}`}>
            {t.text}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  )
}

export function useToast() {
  const ctx = useContext(ToastContext)
  if (!ctx) throw new Error('useToast must be used within a ToastProvider')
  return ctx
}
