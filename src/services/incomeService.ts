import api from './api'
import type { Income } from '@/types/finance'

// Full CRUD wired to the matching Spring Boot IncomeController — use this as
// the template for expenseService/budgetService/savingsService below.
export const incomeService = {
  async list(params?: { from?: string; to?: string; sortBy?: string; direction?: string }) {
    const { data } = await api.get<Income[]>('/incomes', { params })
    return data
  },
  async create(payload: Omit<Income, 'id'>) {
    const { data } = await api.post<Income>('/incomes', payload)
    return data
  },
  async update(id: string, payload: Omit<Income, 'id'>) {
    const { data } = await api.put<Income>(`/incomes/${id}`, payload)
    return data
  },
  async remove(id: string) {
    await api.delete(`/incomes/${id}`)
  }
}
