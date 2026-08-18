import api from './api'
import type { Category } from '@/types/category'

export const categoryService = {
  async list() {
    const { data } = await api.get<Category[]>('/categories')
    return data
  }
}
