import { useEffect, useState, type FormEvent } from 'react'
import { budgetService } from '@/services/budgetService'
import { categoryService } from '@/services/categoryService'
import type { Budget } from '@/types/finance'
import type { Category } from '@/types/category'
import Loader from '@/components/common/Loader'
import EmptyState from '@/components/common/EmptyState'
import Modal from '@/components/common/Modal'
import ConfirmDialog from '@/components/common/ConfirmDialog'
import { useToast } from '@/components/common/Toast'

const now = new Date()
const emptyForm = { categoryId: '', limitAmount: '' }

export default function Budgets() {
  const { notify } = useToast()
  const [month, setMonth] = useState(now.getMonth() + 1)
  const [year, setYear] = useState(now.getFullYear())
  const [budgets, setBudgets] = useState<Budget[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [form, setForm] = useState(emptyForm)
  const [saving, setSaving] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState<Budget | null>(null)

  const load = () => {
    setLoading(true)
    Promise.all([
      budgetService.listForMonth(month, year),
      categories.length ? Promise.resolve(categories) : categoryService.list()
    ])
      .then(([budgetData, categoryData]) => {
        setBudgets(budgetData)
        setCategories(categoryData)
      })
      .catch(() => notify('error', 'Could not load budgets.'))
      .finally(() => setLoading(false))
  }

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(load, [month, year])

  const openCreate = () => {
    setForm(emptyForm)
    setModalOpen(true)
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setSaving(true)
    try {
      await budgetService.create({
        categoryId: form.categoryId || null,
        month,
        year,
        limitAmount: Number(form.limitAmount)
      })
      notify('success', 'Budget created.')
      setModalOpen(false)
      load()
    } catch {
      notify('error', 'Could not save budget. There may already be a budget for that scope.')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await budgetService.remove(deleteTarget.id)
      notify('success', 'Budget deleted.')
      setDeleteTarget(null)
      load()
    } catch {
      notify('error', 'Could not delete budget.')
    }
  }

  const currency = (n: number) => n.toLocaleString(undefined, { style: 'currency', currency: 'USD' })

  if (loading) return <Loader label="Loading budgets…" />

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <h1 className="font-display text-2xl text-ink">Budgets</h1>
        <div className="flex items-center gap-2">
          <select value={month} onChange={(e) => setMonth(Number(e.target.value))}
                  className="rounded-md border border-ledger-100 px-3 py-2 text-sm">
            {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
              <option key={m} value={m}>{new Date(2000, m - 1).toLocaleString('default', { month: 'long' })}</option>
            ))}
          </select>
          <select value={year} onChange={(e) => setYear(Number(e.target.value))}
                  className="rounded-md border border-ledger-100 px-3 py-2 text-sm">
            {[year - 1, year, year + 1].map((y) => <option key={y} value={y}>{y}</option>)}
          </select>
          <button onClick={openCreate} className="rounded-md bg-ledger-500 px-4 py-2 text-sm text-white hover:bg-ledger-600">
            + Add Budget
          </button>
        </div>
      </div>

      {budgets.length === 0 ? (
        <EmptyState title="No budgets set for this month" description="Create a budget to start tracking spending against a limit." />
      ) : (
        <div className="grid gap-4 md:grid-cols-2">
          {budgets.map((b) => {
            const pct = Math.min(b.percentUsed, 100)
            return (
              <div key={b.id} className="rounded-lg bg-white p-5 shadow-sm">
                <div className="flex items-start justify-between">
                  <div>
                    <p className="font-display text-lg text-ink">{b.categoryName ?? 'Overall Budget'}</p>
                    <p className="text-sm text-ink/60">{currency(b.spentAmount)} of {currency(b.limitAmount)}</p>
                  </div>
                  <button onClick={() => setDeleteTarget(b)} className="text-sm text-signal-expense hover:underline">
                    Delete
                  </button>
                </div>
                <div className="mt-3 h-2 w-full overflow-hidden rounded-full bg-ledger-50">
                  <div
                    className={`h-full rounded-full ${b.exceeded ? 'bg-signal-expense' : 'bg-ledger-500'}`}
                    style={{ width: `${pct}%` }}
                  />
                </div>
                <p className={`mt-2 text-sm ${b.exceeded ? 'text-signal-expense' : 'text-ink/60'}`}>
                  {b.exceeded
                    ? `Budget exceeded by ${currency(Math.abs(b.remainingAmount))}`
                    : `${currency(b.remainingAmount)} remaining`}
                </p>
              </div>
            )
          })}
        </div>
      )}

      <Modal open={modalOpen} title="Add Budget" onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit} className="space-y-3">
          <label className="block text-sm text-ink/70">
            Category (leave blank for an overall monthly budget)
            <select
              value={form.categoryId}
              onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
            >
              <option value="">Overall budget</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </label>
          <label className="block text-sm text-ink/70">
            Limit amount
            <input
              type="number" step="0.01" min="0.01" required
              value={form.limitAmount}
              onChange={(e) => setForm({ ...form, limitAmount: e.target.value })}
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
        title="Delete budget?"
        message="This will permanently delete this budget."
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        confirmLabel="Delete"
      />
    </div>
  )
}
