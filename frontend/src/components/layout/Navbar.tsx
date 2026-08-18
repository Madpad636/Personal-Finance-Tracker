import { useAuth } from '@/context/AuthContext'
import { useNavigate } from 'react-router-dom'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = async () => {
    await logout()
    navigate('/login')
  }

  return (
    <header className="flex h-16 items-center justify-between border-b border-ledger-100 bg-white/80 px-6">
      <span className="font-display text-lg text-ledger-600">Personal Finance Manager</span>
      <div className="flex items-center gap-4">
        <span className="text-sm text-ink/70">{user?.fullName}</span>
        <button onClick={handleLogout} className="text-sm text-signal-expense hover:underline">
          Log out
        </button>
      </div>
    </header>
  )
}
