import { useEffect, useState, type FormEvent } from 'react'
import { incomeService } from '@/services/incomeService'
import type { Income as IncomeType } from '@/types/finance'
import Loader from '@/components/common/Loader'
import EmptyState from '@/components/common/EmptyState'
import Modal from '@/components/common/Modal'
import ConfirmDialog from '@/components/common/ConfirmDialog'
import { useToast } from '@/components/common/Toast'

const emptyForm = { amount: '', source: '', date: '', description: '' }

export default function Income() {
  const { notify } = useToast()
  const [incomes, setIncomes] = useState<IncomeType[]>([])
  const [loading, setLoading] = useState(true)
  const [modalOpen, setModalOpen] = useState(false)
  const [editingId, setEditingId] = useState<string | null>(null)
  const [form, setForm] = useState(emptyForm)
  const [saving, setSaving] = useState(false)
  const [deleteTarget, setDeleteTarget] = useState<IncomeType | null>(null)
  const [search, setSearch] = useState('')

  const load = () => {
    setLoading(true)
    incomeService
      .list({ sortBy: 'date', direction: 'desc' })
      .then(setIncomes)
      .catch(() => notify('error', 'Could not load income records.'))
      .finally(() => setLoading(false))
  }

  useEffect(load, [])

  const openCreate = () => {
    setEditingId(null)
    setForm(emptyForm)
    setModalOpen(true)
  }

  const openEdit = (income: IncomeType) => {
    setEditingId(income.id)
    setForm({
      amount: String(income.amount),
      source: income.source,
      date: income.date,
      description: income.description ?? ''
    })
    setModalOpen(true)
  }

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setSaving(true)
    const payload = {
      amount: Number(form.amount),
      source: form.source,
      date: form.date,
      description: form.description
    }
    try {
      if (editingId) {
        await incomeService.update(editingId, payload)
        notify('success', 'Income updated.')
      } else {
        await incomeService.create(payload)
        notify('success', 'Income added.')
      }
      setModalOpen(false)
      load()
    } catch {
      notify('error', 'Could not save income. Check the form and try again.')
    } finally {
      setSaving(false)
    }
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    try {
      await incomeService.remove(deleteTarget.id)
      notify('success', 'Income deleted.')
      setDeleteTarget(null)
      load()
    } catch {
      notify('error', 'Could not delete income.')
    }
  }

  const filtered = incomes.filter(
    (i) =>
      i.source.toLowerCase().includes(search.toLowerCase()) ||
      (i.description ?? '').toLowerCase().includes(search.toLowerCase())
  )

  if (loading) return <Loader label="Loading income…" />

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between gap-4">
        <h1 className="font-display text-2xl text-ink">Income</h1>
        <button onClick={openCreate} className="rounded-md bg-ledger-500 px-4 py-2 text-sm text-white hover:bg-ledger-600">
          + Add Income
        </button>
      </div>

      <input
        placeholder="Search by source or description…"
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        className="w-full max-w-sm rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
      />

      {filtered.length === 0 ? (
        <EmptyState
          title={incomes.length === 0 ? 'No income recorded yet' : 'No matches'}
          description={incomes.length === 0 ? 'Add your first income entry to see it here.' : 'Try a different search term.'}
        />
      ) : (
        <div className="overflow-hidden rounded-lg bg-white shadow-sm">
          <table className="w-full text-left text-sm">
            <thead className="bg-ledger-50 text-ink/60">
              <tr>
                <th className="px-4 py-3">Date</th>
                <th className="px-4 py-3">Source</th>
                <th className="px-4 py-3">Description</th>
                <th className="px-4 py-3 text-right">Amount</th>
                <th className="px-4 py-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-ledger-50">
              {filtered.map((income) => (
                <tr key={income.id}>
                  <td className="px-4 py-3">{income.date}</td>
                  <td className="px-4 py-3">{income.source}</td>
                  <td className="px-4 py-3 text-ink/60">{income.description}</td>
                  <td className="px-4 py-3 text-right text-signal-income">
                    {income.amount.toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button onClick={() => openEdit(income)} className="mr-3 text-ledger-500 hover:underline">
                      Edit
                    </button>
                    <button onClick={() => setDeleteTarget(income)} className="text-signal-expense hover:underline">
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <Modal open={modalOpen} title={editingId ? 'Edit Income' : 'Add Income'} onClose={() => setModalOpen(false)}>
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
            Source
            <input
              required
              value={form.source}
              onChange={(e) => setForm({ ...form, source: e.target.value })}
              className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
            />
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
        title="Delete income record?"
        message={`This will permanently delete the ${deleteTarget?.source ?? ''} entry.`}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        confirmLabel="Delete"
      />
    </div>
  )
}
