import { useState } from 'react'
import { reportService } from '@/services/reportService'
import { useToast } from '@/components/common/Toast'

const now = new Date()

export default function Reports() {
  const { notify } = useToast()
  const [month, setMonth] = useState(now.getMonth() + 1)
  const [year, setYear] = useState(now.getFullYear())
  const [downloading, setDownloading] = useState<'csv' | 'pdf' | null>(null)

  const handleDownload = async (format: 'csv' | 'pdf') => {
    setDownloading(format)
    try {
      if (format === 'csv') {
        await reportService.downloadMonthlyCsv(month, year)
      } else {
        await reportService.downloadMonthlyPdf(month, year)
      }
      notify('success', `Report downloaded as ${format.toUpperCase()}.`)
    } catch {
      notify('error', 'Could not generate the report.')
    } finally {
      setDownloading(null)
    }
  }

  return (
    <div className="space-y-6">
      <h1 className="font-display text-2xl text-ink">Reports</h1>

      <div className="max-w-md rounded-lg bg-white p-6 shadow-sm">
        <p className="font-display text-lg text-ink">Monthly report</p>
        <p className="mt-1 text-sm text-ink/60">
          Includes every income and expense entry for the selected month, plus totals and net balance.
        </p>

        <div className="mt-4 flex gap-3">
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
        </div>

        <div className="mt-5 flex gap-3">
          <button
            onClick={() => handleDownload('csv')}
            disabled={downloading !== null}
            className="rounded-md bg-ledger-500 px-4 py-2 text-sm text-white hover:bg-ledger-600 disabled:opacity-60"
          >
            {downloading === 'csv' ? 'Preparing…' : 'Download CSV'}
          </button>
          <button
            onClick={() => handleDownload('pdf')}
            disabled={downloading !== null}
            className="rounded-md border border-ledger-500 px-4 py-2 text-sm text-ledger-600 hover:bg-ledger-50 disabled:opacity-60"
          >
            {downloading === 'pdf' ? 'Preparing…' : 'Download PDF'}
          </button>
        </div>
      </div>

      <p className="text-sm text-ink/40">
        Category-wise spending and savings reports reuse the same ReportService — extend
        exportMonthlyReportCsv/Pdf on the backend to add more report types here.
      </p>
    </div>
  )
}
