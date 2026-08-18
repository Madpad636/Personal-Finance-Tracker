import api from './api'
import type { Budget } from '@/types/finance'

// TODO: backend BudgetService/Controller are stubbed — wire up once implemented.
export const budgetService = {
  async listForMonth(month: number, year: number) {
    const { data } = await api.get<Budget[]>('/budgets', { params: { month, year } })
    return data
  },
  async create(payload: { categoryId: string | null; month: number; year: number; limitAmount: number }) {
    const { data } = await api.post<Budget>('/budgets', payload)
    return data
  },
  async update(id: string, payload: { categoryId: string | null; month: number; year: number; limitAmount: number }) {
    const { data } = await api.put<Budget>(`/budgets/${id}`, payload)
    return data
  },
  async remove(id: string) {
    await api.delete(`/budgets/${id}`)
  }
}
