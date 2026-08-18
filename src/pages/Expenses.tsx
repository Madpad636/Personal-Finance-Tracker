import { useEffect, useState, type FormEvent } from 'react'
import { expenseService } from '@/services/expenseService'
import { categoryService } from '@/services/categoryService'
import type { Expense } from '@/types/finance'
import type { Category } from '@/types/category'
import Loader from '@/components/common/Loader'
import EmptyState from '@/components/common/EmptyState'
import Modal from '@/components/common/Modal'
import ConfirmDialog from '@/components/common/ConfirmDialog'
import { useToast } from '@/components/common/Toast'

const emptyForm = { amount: '', categoryId: '', date: '', description: '' }

export default function Expenses() {
  const { notify } = useToast()
  const [expenses, setExpenses] = useState<Expense[]>([])
  const [categories, setCategories] = useState<Category[]>([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editingId, setEditingId] = useState<string | null>(null)
  const [form, setForm] = useState(emptyForm)
  const [saving, setSaving] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState<Expense | null>(null)
  const [categoryFilter, setCategoryFilter] = useState('')
  const [search, setSearch] = useState('')

  const load = () => {
    setLoading(true)
    Promise.all([
      expenseService.list(categoryFilter ? { categoryId: categoryFilter } : undefined),
      categories.length ? Promise.resolve(categories) : categoryService.list()
    ])
      .then(([expenseData, categoryData]) => {
        setExpenses(expenseData)
        setCategories(categoryData)
      })
      .catch(() => notify('error', 'Could not load expenses.'))
      .finally(() => setLoading(false))
  }

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(load, [categoryFilter])

  const openCreate = () => {
    setEditingId(null)
    setForm({ ...emptyForm, categoryId: categories[0]?.id ?? '' })
    setModalOpen(true)
  }

  const openEdit = (expense: Expense) => {
    setEditingId(expense.id)
    setForm({
      amount: String(expense.amount),
      categoryId: expense.categoryId,
      date: expense.date,
      description: expense.description ?? ''
    })
    setModalOpen(true)
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setSaving(true)
    const payload = {
      amount: Number(form.amount),
      categoryId: form.categoryId,
      date: form.date,
      description: form.description
    }
    try {
      if (editingId) {
        await expenseService.update(editingId, payload)
        notify('success', 'Expense updated.')
      } else {
        await expenseService.create(payload)
        notify('success', 'Expense added.')
      }
      setModalOpen(false)
      load()
    } catch {
      notify('error', 'Could not save expense. Check the form and try again.')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await expenseService.remove(deleteTarget.id)
      notify('success', 'Expense deleted.')
      setDeleteTarget(null)
      load()
    } catch {
      notify('error', 'Could not delete expense.')
    }
  }

  const filtered = expenses.filter((e) => (e.description ?? '').toLowerCase().includes(search.toLowerCase()))

  if (loading) return <Loader label="Loading expenses…" />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between gap-4">
        <h1 className="font-display text-2xl text-ink">Expenses</h1>
        <button onClick={openCreate} className="rounded-md bg-ledger-500 px-4 py-2 text-sm text-white hover:bg-ledger-600">
          + Add Expense
        </button>
      </div>

      <div className="flex flex-wrap gap-3">
        <input
          placeholder="Search description…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full max-w-sm rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
        />
        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          className="rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
        >
          <option value="">All categories</option>
          {categories.map((c) => (
            <option key={c.id} value={c.id}>{c.name}</option>
          ))}
        </select>
      </div>

      {filtered.length === 0 ? (
        <EmptyState
          title={expenses.length === 0 ? 'No expenses recorded yet' : 'No matches'}
          description={expenses.length === 0 ? 'Add your first expense to see it here.' : 'Try a different search or filter.'}
        />
      ) : (
        <div className="overflow-hidden rounded-lg bg-white shadow-sm">
          <table className="w-full text-left text-sm">
            <thead className="bg-ledger-50 text-ink/60">
              <tr>
                <th className="px-4 py-3">Date</th>
                <th className="px-4 py-3">Category</th>
                <th className="px-4 py-3">Description</th>
                <th className="px-4 py-3 text-right">Amount</th>
                <th className="px-4 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-ledger-50">
              {filtered.map((expense) => (
                <tr key={expense.id}>
                  <td className="px-4 py-3">{expense.date}</td>
                  <td className="px-4 py-3">{expense.categoryName}</td>
                  <td className="px-4 py-3 text-ink/60">{expense.description}</td>
                  <td className="px-4 py-3 text-right text-signal-expense">
                    {expense.amount.toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button onClick={() => openEdit(expense)} className="mr-3 text-ledger-500 hover:underline">
                      Edit
                    </button>
                    <button onClick={() => setDeleteTarget(expense)} className="text-signal-expense hover:underline">
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <Modal open={modalOpen} title={editingId ? 'Edit Expense' : 'Add Expense'} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleSubmit} className="space-y-3">
          <label className="block text-sm text-ink/70">
            Amount
            <input
              type="number" step="0.01" min="0.01" required
              value={form.amount}
              onChange={(e) => setForm({ ...form, amount: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
            />
          </label>
          <label className="block text-sm text-ink/70">
            Category
            <select
              required
              value={form.categoryId}
              onChange={(e) => setForm({ ...form, categoryId: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
            >
              <option value="" disabled>Select a category</option>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </label>
          <label className="block text-sm text-ink/70">
            Date
            <input
              type="date" required
              value={form.date}
              onChange={(e) => setForm({ ...form, date: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
            />
          </label>
          <label className="block text-sm text-ink/70">
            Description
            <textarea
              value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
              rows={2}
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
        title="Delete expense record?"
        message={`This will permanently delete this ${deleteTarget?.categoryName ?? ''} expense.`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        confirmLabel="Delete"
      />
    </div>
  )
}
