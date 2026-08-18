import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '@/context/AuthContext'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await login({ email, password })
      navigate('/dashboard')
    } catch (err: any) {
      setError(err?.response?.data?.message ?? 'Login failed. Please check your credentials.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-paper px-4">
      <form onSubmit={handleSubmit} className="w-full max-w-sm rounded-lg bg-white p-8 shadow-sm">
        <h1 className="font-display text-2xl text-ledger-600">Welcome back</h1>
        <p className="mt-1 text-sm text-ink/60">Log in to manage your finances.</p>

        {error && <p className="mt-4 rounded-md bg-signal-expense/10 px-3 py-2 text-sm text-signal-expense">{error}</p>}

        <label className="mt-6 block text-sm text-ink/70">
          Email
          <input
            type="email"
            required
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
          />
        </label>

        <label className="mt-4 block text-sm text-ink/70">
          Password
          <input
            type="password"
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="mt-1 w-full rounded-md border border-ledger-100 px-3 py-2 text-sm focus:border-ledger-500 focus:outline-none"
          />
        </label>

        <button
          type="submit"
          disabled={loading}
          className="mt-6 w-full rounded-md bg-ledger-500 py-2 text-sm font-medium text-white hover:bg-ledger-600 disabled:opacity-60"
        >
          {loading ? 'Logging in…' : 'Log in'}
        </button>

        <p className="mt-4 text-center text-sm text-ink/60">
          No account? <Link to="/register" className="text-ledger-500 hover:underline">Register</Link>
        </p>
      </form>
    </div>
  )
}
