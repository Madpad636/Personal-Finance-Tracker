export interface AuthUser {
  userId: string
  fullName: string
  email: string
}

export interface AuthResponse extends AuthUser {
  accessToken: string
  refreshToken: string
  tokenType: 'Bearer'
}

export interface LoginPayload {
  email: string
  password: string
}

export interface RegisterPayload {
  fullName: string
  email: string
  password: string
}
