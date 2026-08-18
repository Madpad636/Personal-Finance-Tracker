import api from './api'
import type { Expense } from '@/types/finance'

// TODO: backend ExpenseService/Controller are stubbed — wire up once implemented.
export const expenseService = {
  async list(params?: { from?: string; to?: string; categoryId?: string; sortBy?: string; direction?: string }) {
    const { data } = await api.get<Expense[]>('/expenses', { params })
    return data
  },
  async create(payload: Omit<Expense, 'id' | 'categoryName'>) {
    const { data } = await api.post<Expense>('/expenses', payload)
    return data
  },
  async update(id: string, payload: Omit<Expense, 'id' | 'categoryName'>) {
    const { data } = await api.put<Expense>(`/expenses/${id}`, payload)
    return data
  },
  async remove(id: string) {
    await api.delete(`/expenses/${id}`)
  }
}
