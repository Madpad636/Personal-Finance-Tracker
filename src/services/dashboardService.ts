import api from './api'
import type { DashboardSummary } from '@/types/finance'

// TODO: backend DashboardService is stubbed — wire up once implemented.
export const dashboardService = {
  async getSummary(month: number, year: number) {
    const { data } = await api.get<DashboardSummary>('/dashboard/summary', { params: { month, year } })
    return data
  }
}
