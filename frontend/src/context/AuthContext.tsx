import { createContext, useContext, useState, useCallback, type ReactNode } from 'react'
import { authService } from '@/services/authService'
import type { AuthUser, LoginPayload, RegisterPayload } from '@/types/auth'

interface AuthContextValue {
  user: AuthUser | null
  isAuthenticated: boolean
  login: (payload: LoginPayload) => Promise<void>
  register: (payload: RegisterPayload) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined)

function loadStoredUser(): AuthUser | null {
  const raw = localStorage.getItem('authUser')
  return raw ? (JSON.parse(raw) as AuthUser) : null
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(loadStoredUser())

  const persist = (auth: { userId: string; fullName: string; email: string; accessToken: string; refreshToken: string }) => {
    localStorage.setItem('accessToken', auth.accessToken)
    localStorage.setItem('refreshToken', auth.refreshToken)
    const authUser = { userId: auth.userId, fullName: auth.fullName, email: auth.email }
    localStorage.setItem('authUser', JSON.stringify(authUser))
    setUser(authUser)
  }

  const login = useCallback(async (payload: LoginPayload) => {
    const response = await authService.login(payload)
    persist(response)
  }, [])

  const register = useCallback(async (payload: RegisterPayload) => {
    const response = await authService.register(payload)
    persist(response)
  }, [])

  const logout = useCallback(async () => {
    try {
      await authService.logout()
    } finally {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('authUser')
      setUser(null)
    }
  }, [])

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider')
  return ctx
}
