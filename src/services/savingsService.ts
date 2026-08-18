import api from './api'
import type { SavingsGoal } from '@/types/finance'

// TODO: backend SavingsGoalService/Controller are stubbed — wire up once implemented.
export const savingsService = {
  async list() {
    const { data } = await api.get<SavingsGoal[]>('/savings-goals')
    return data
  },
  async create(payload: { name: string; targetAmount: number; currentAmount?: number; targetDate?: string }) {
    const { data } = await api.post<SavingsGoal>('/savings-goals', payload)
    return data
  },
  async remove(id: string) {
    await api.delete(`/savings-goals/${id}`)
  }
}
