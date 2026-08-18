import api from './api'
import type { AuthResponse, LoginPayload, RegisterPayload } from '@/types/auth'

export const authService = {
  async register(payload: RegisterPayload): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/register', payload)
    return data
  },
  async login(payload: LoginPayload): Promise<AuthResponse> {
    const { data } = await api.post<AuthResponse>('/auth/login', payload)
    return data
  },
  async logout(): Promise<void> {
    await api.post('/auth/logout')
  },
  async changePassword(currentPassword: string, newPassword: string): Promise<void> {
    await api.post('/auth/change-password', { currentPassword, newPassword })
  }
}
