import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '@/context/AuthContext'

/** Wraps private routes; redirects to /login when there's no authenticated user. */
export default function ProtectedRoute() {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? <Outlet /> : <Navigate to="/login" replace />
}
