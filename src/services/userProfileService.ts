import api from './api'

export interface UserProfile {
  id: string
  fullName: string
  email: string
  createdAt: string
}

export const userProfileService = {
  async get() {
    const { data } = await api.get<UserProfile>('/profile')
    return data
  },
  async update(fullName: string) {
    const { data } = await api.put<UserProfile>('/profile', { fullName })
    return data
  }
}
