import { useEffect, useState, type FormEvent } from 'react'
import { savingsService } from '@/services/savingsService'
import type { SavingsGoal } from '@/types/finance'
import Loader from '@/components/common/Loader'
import EmptyState from '@/components/common/EmptyState'
import Modal from '@/components/common/Modal'
import ConfirmDialog from '@/components/common/ConfirmDialog'
import { useToast } from '@/components/common/Toast'

const emptyForm = { name: '', targetAmount: '', currentAmount: '', targetDate: '' }

export default function Savings() {
  const { notify } = useToast()
  const [goals, setGoals] = useState<SavingsGoal[]>([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(emptyForm)
  const [saving, setSaving] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState<SavingsGoal | null>(null)

  const load = () => {
    setLoading(true)
    savingsService.list().then(setGoals).catch(() => notify('error', 'Could not load savings goals.')).finally(() => setLoading(false))
  }

  useEffect(load, [])

  const openCreate = () => {
    setForm(emptyForm)
    setModalOpen(true)
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setSaving(true)
    try {
      await savingsService.create({
        name: form.name,
        targetAmount: Number(form.targetAmount),
        currentAmount: form.currentAmount ? Number(form.currentAmount) : 0,
        targetDate: form.targetDate || undefined
      })
      notify('success', 'Savings goal created.')
      setModalOpen(false)
      load()
    } catch {
      notify('error', 'Could not save goal. Check the form and try again.')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await savingsService.remove(deleteTarget.id)
      notify('success', 'Savings goal deleted.')
      setDeleteTarget(null)
      load()
    } catch {
      notify('error', 'Could not delete goal.')
    }
  }

  const currency = (n: number) => n.toLocaleString(undefined, { style: 'currency', currency: 'USD' })

  if (loading) return <Loader label="Loading savings goals…" />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between gap-4">
        <h1 className="font-display text-2xl text-ink">Savings Goals</h1>
        <button onClick={openCreate} className="rounded-md bg-ledger-500 px-4 py-2 text-sm text-white hover:bg-ledger-600">
          + Add Goal
        </button>
      </div>

      {goals.length === 0 ? (
        <EmptyState title="No savings goals yet" description="Set a target and start tracking progress toward it." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {goals.map((g) => (
            <div key={g.id} className="rounded-lg bg-white p-5 shadow-sm">
              <div className="flex items-start justify-between">
                <div>
                  <p className="font-display text-lg text-ink">{g.name}</p>
                  <p className="text-sm text-ink/60">
                    {currency(g.currentAmount)} of {currency(g.targetAmount)}
                    {g.targetDate && <> · target {g.targetDate}</>}
                  </p>
                </div>
                <button onClick={() => setDeleteTarget(g)} className="text-sm text-signal-expense hover:underline">
                  Delete
                </button>
              </div>
              <div className="mt-3 h-2 w-full overflow-hidden rounded-full bg-ledger-50">
                <div className="h-full rounded-full bg-signal-income" style={{ width: `${g.percentComplete}%` }} />
              </div>
              <p className="mt-2 text-sm text-ink/60">
                {g.percentComplete.toFixed(0)}% complete · {currency(g.remainingAmount)} to go
              </p>
            </div>
          ))}
        </div>
      )}

      <Modal open={modalOpen} title="Add Savings Goal" onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit} className="space-y-3">
          <label className="block text-sm text-ink/70">
            Goal name
            <input
              required
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
            />
          </label>
          <label className="block text-sm text-ink/70">
            Target amount
            <input
              type="number" step="0.01" min="0.01" required
              value={form.targetAmount}
              onChange={(e) => setForm({ ...form, targetAmount: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
            />
          </label>
          <label className="block text-sm text-ink/70">
            Current amount (optional)
            <input
              type="number" step="0.01" min="0"
              value={form.currentAmount}
              onChange={(e) => setForm({ ...form, currentAmount: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
            />
          </label>
          <label className="block text-sm text-ink/70">
            Target date (optional)
            <input
              type="date"
              value={form.targetDate}
              onChange={(e) => setForm({ ...form, targetDate: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
            />
          </label>
          <div className="flex justify-end gap-3 pt-2">
            <button type="button" onClick={() => setModalOpen(false)} className="rounded-md px-4 py-2 text-sm text-ink/70 hover:bg-ink/5">
              Cancel
            </button>
            <button type="submit" disabled={saving} className="rounded-md bg-ledger-500 px-4 py-2 text-sm text-white hover:bg-ledger-600 disabled:opacity-60">
              {saving ? 'Saving…' : 'Save'}
            </button>
          </div>
        </form>
      </Modal>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Delete savings goal?"
        message={`This will permanently delete "${deleteTarget?.name ?? ''}".`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        confirmLabel="Delete"
      />
    </div>
  )
}
