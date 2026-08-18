import { useEffect, useState } from 'react'
import { PieChart, Pie, Cell, BarChart, Bar, LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts'
import { dashboardService } from '@/services/dashboardService'
import { expenseService } from '@/services/expenseService'
import { incomeService } from '@/services/incomeService'
import type { DashboardSummary } from '@/types/finance'
import Loader from '@/components/common/Loader'
import EmptyState from '@/components/common/EmptyState'

const CATEGORY_COLORS = ['#2F6657', '#4C8577', '#C98A2C', '#B24C3A', '#6B7280', '#9CA3AF', '#1D3F38', '#D7E4DE']

function currentMonthKey() {
  return new Date().toISOString().slice(0, 7)
}

function monthKeyFromDate(date: string) {
  return date.slice(0, 7)
}

function parseMonthKey(monthKey: string) {
  const [year, month] = monthKey.split('-').map(Number)
  return { month, year }
}

function formatMonthLabel(monthKey: string) {
  const [year, month] = monthKey.split('-').map(Number)
  return new Date(year, month - 1).toLocaleString(undefined, { month: 'long', year: 'numeric' })
}

function StatCard({ label, value, tone }: { label: string; value: string; tone?: 'income' | 'expense' }) {
  return (
    <div className="rounded-lg bg-white p-5 shadow-sm">
      <p className="text-sm text-ink/60">{label}</p>
      <p className={`mt-1 font-display text-2xl ${tone === 'income' ? 'text-signal-income' : tone === 'expense' ? 'text-signal-expense' : 'text-ink'}`}>
        {value}
      </p>
    </div>
  )
}

export default function Dashboard() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [selectedMonth, setSelectedMonth] = useState(currentMonthKey())

  const loadSummary = (monthKey: string) => {
    setLoading(true)
    setError(null)
    const { month, year } = parseMonthKey(monthKey)
    dashboardService
      .getSummary(month, year)
      .then(setSummary)
      .catch(() => setError('Could not load dashboard summary.'))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    Promise.all([
      incomeService.list({ sortBy: 'date', direction: 'desc' }),
      expenseService.list({ sortBy: 'date', direction: 'desc' })
    ])
      .then(([incomes, expenses]) => {
        const dates = [...incomes.map((i) => i.date), ...expenses.map((e) => e.date)].sort()
        const latestDate = dates.length > 0 ? dates[dates.length - 1] : undefined
        const monthKey = latestDate ? monthKeyFromDate(latestDate) : currentMonthKey()
        setSelectedMonth(monthKey)
        loadSummary(monthKey)
      })
      .catch(() => loadSummary(selectedMonth))
  }, [])

  useEffect(() => {
    const reload = () => loadSummary(selectedMonth)
    window.addEventListener('focus', reload)
    return () => window.removeEventListener('focus', reload)
  }, [selectedMonth])

  if (loading) return <Loader label="Loading dashboard..." />

  if (error || !summary) {
    return (
      <EmptyState
        title="Dashboard data isn't available yet"
        description={error ?? 'Choose a month with income or expenses to see totals and charts.'}
      />
    )
  }

  const currency = (n: number) => n.toLocaleString(undefined, { style: 'currency', currency: 'USD' })

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="font-display text-2xl text-ink">Dashboard</h1>
          <p className="mt-1 text-sm text-ink/60">Showing totals for {formatMonthLabel(selectedMonth)}.</p>
        </div>
        <label className="text-sm text-ink/70">
          Month
          <input
            type="month"
            value={selectedMonth}
            onChange={(e) => {
              setSelectedMonth(e.target.value)
              loadSummary(e.target.value)
            }}
            className="mt-1 block rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
          />
        </label>
      </div>

      <div className="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-5">
        <StatCard label="Monthly Income" value={currency(summary.totalIncome)} tone="income" />
        <StatCard label="Monthly Expenses" value={currency(summary.totalExpenses)} tone="expense" />
        <StatCard label="Net Balance" value={currency(summary.remainingBalance)} />
        <StatCard label="Monthly Budget" value={currency(summary.monthlyBudget)} />
        <StatCard label="Total Savings" value={currency(summary.totalSavings)} tone="income" />
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        <div className="rounded-lg bg-white p-5 shadow-sm">
          <h2 className="mb-4 font-display text-lg text-ink">Spending by category</h2>
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={summary.spendingByCategory} dataKey="amount" nameKey="category" outerRadius={90}>
                {summary.spendingByCategory.map((_, i) => (
                  <Cell key={i} fill={CATEGORY_COLORS[i % CATEGORY_COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="rounded-lg bg-white p-5 shadow-sm">
          <h2 className="mb-4 font-display text-lg text-ink">Income vs. expenses</h2>
          <ResponsiveContainer width="100%" height={260}>
            <BarChart data={summary.monthlyTrend}>
              <XAxis dataKey="month" stroke="#1B2430" fontSize={12} />
              <YAxis stroke="#1B2430" fontSize={12} />
              <Tooltip />
              <Bar dataKey="income" fill="#2F6657" radius={[4, 4, 0, 0]} />
              <Bar dataKey="expenses" fill="#B24C3A" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="rounded-lg bg-white p-5 shadow-sm lg:col-span-2">
          <h2 className="mb-4 font-display text-lg text-ink">Monthly trend</h2>
          <ResponsiveContainer width="100%" height={240}>
            <LineChart data={summary.monthlyTrend}>
              <XAxis dataKey="month" stroke="#1B2430" fontSize={12} />
              <YAxis stroke="#1B2430" fontSize={12} />
              <Tooltip />
              <Line type="monotone" dataKey="income" stroke="#2F6657" strokeWidth={2} />
              <Line type="monotone" dataKey="expenses" stroke="#B24C3A" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  )
}
