import api from './api'

export const reportService = {
  async downloadMonthlyCsv(month: number, year: number) {
    const { data } = await api.get(`/reports/monthly/csv`, { params: { month, year }, responseType: 'blob' })
    triggerDownload(data, `report-${year}-${month}.csv`)
  },
  async downloadMonthlyPdf(month: number, year: number) {
    const { data } = await api.get(`/reports/monthly/pdf`, { params: { month, year }, responseType: 'blob' })
    triggerDownload(data, `report-${year}-${month}.pdf`)
  }
}

function triggerDownload(blob: Blob, filename: string) {
  const url = window.URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  link.remove()
  window.URL.revokeObjectURL(url)
}
