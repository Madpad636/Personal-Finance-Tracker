import { Navigate, Route, Routes } from 'react-router-dom'
import ProtectedRoute from '@/components/common/ProtectedRoute'
import AppLayout from '@/components/layout/AppLayout'
import Login from '@/pages/Login'
import Register from '@/pages/Register'
import Dashboard from '@/pages/Dashboard'
import Income from '@/pages/Income'
import Expenses from '@/pages/Expenses'
import Budgets from '@/pages/Budgets'
import Savings from '@/pages/Savings'
import Reports from '@/pages/Reports'
import Profile from '@/pages/Profile'
import NotFound from '@/pages/NotFound'

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/income" element={<Income />} />
          <Route path="/expenses" element={<Expenses />} />
          <Route path="/budgets" element={<Budgets />} />
          <Route path="/savings" element={<Savings />} />
          <Route path="/reports" element={<Reports />} />
          <Route path="/profile" element={<Profile />} />
        </Route>
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}
